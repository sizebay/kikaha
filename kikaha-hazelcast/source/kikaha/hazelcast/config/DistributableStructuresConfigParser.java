package kikaha.hazelcast.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lombok.val;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

import com.typesafe.config.Config;

/**
 * This parser class intend to parse an Typesafe Config item and transform into
 * a Hazelcast programmatic configuration item. It is very useful to avoid
 * repetitive programmatic configuration of Hazelcast.<br>
 * <br>
 * You always can use the default {@code hazelcast.xml} configuration file to
 * configure the behavior of your distributable structure under the Hazelcast
 * cluster. The Typesafe Configuration is useful to allow developer to have a
 * centralized way to configure hazelcast and take advantage of tRip's
 * Dependency Injection.<br>
 * <br>
 * Note that it wasn't designed to be thread-safe neither to be as fast as
 * possible. It means this class is useful only during the boot of Hazalcast
 * module and you should avoid use this parsed under intensive loop avoid
 * performance issues.<br>
 * 
 * @author Miere Liniel Teixeira
 */
@Singleton
public class DistributableStructuresConfigParser {

	@Provided
	ServiceProvider provider;

	public Object parse( Config config, Class<?> clazz ) {
		try {
			val delegateTo = clazz.getAnnotation( DelegateTo.class );
			val delegatedClazz = delegateTo.value();
			return parse( config, clazz, delegatedClazz );
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	public <T> T parse( Config config, Class<?> clazz, final java.lang.Class<T> delegatedClazz )
			throws Exception
	{
		val instance = delegatedClazz.newInstance();
		val setterMethods = retrieveSetterMethods( clazz, instance.getClass() );
		for ( val setter : setterMethods )
			setter.set( instance, config );
		return instance;
	}

	List<Setter> retrieveSetterMethods( Class<?> currentClazz, Class<?> targetClazz )
			throws NoSuchMethodException, SecurityException
	{
		val list = new ArrayList<Setter>();
		Class<?> clazz = currentClazz;
		while ( clazz != null && !Object.class.equals( clazz ) ) {
			populateWithSetterMethodsFoundInClass( list, clazz, targetClazz );
			clazz = clazz.getSuperclass();
		}
		return list;
	}

	void populateWithSetterMethodsFoundInClass( ArrayList<Setter> list, Class<?> clazz, Class<?> targetClazz )
			throws NoSuchMethodException, SecurityException
	{
		for ( val method : clazz.getDeclaredMethods() ) {
			val returnType = retrieveFirstParameterFrom( method );
			val configItem = method.getAnnotation( ConfigItem.class );
			val setter = createSetter( method, targetClazz, configItem.value(), returnType );
			if ( setter != null )
				list.add( setter );
		}
	}

	Class<?> retrieveFirstParameterFrom( final Method method ) {
		for ( val clazz : method.getParameterTypes() )
			return clazz;
		throw new IllegalStateException( method.getName() + " should have one parameter." );
	}

	Setter createSetter( Method method, Class<?> targetClazz, String configPath, Class<?> parameterType )
			throws NoSuchMethodException, SecurityException
	{
		val delegateTo = parameterType.getAnnotation( DelegateTo.class );
		if ( delegateTo != null )
			return createDelegatedSetter( method, targetClazz, configPath, parameterType, delegateTo );
		return createStandardSetter( method, targetClazz, configPath, parameterType );
	}

	Setter createDelegatedSetter( Method method, Class<?> targetClazz, String configPath,
			Class<?> parameterType, DelegateTo delegateTo )
			throws NoSuchMethodException
	{
		val delegatedTargetMethod = targetClazz.getMethod( method.getName(), delegateTo.value() );
		if ( method.isAnnotationPresent( CallMethodForEachFoundEntry.class ) )
			return listDelegatedSetter( delegatedTargetMethod, configPath, parameterType );
		return delegatedSetter( delegatedTargetMethod, configPath, parameterType );
	}

	Setter createStandardSetter( Method method, Class<?> targetClazz, String configPath, Class<?> parameterType )
			throws NoSuchMethodException
	{
		val targetMethod = targetClazz.getMethod( method.getName(), parameterType );
		if ( parameterType.isEnum() )
			return enumeratorSetter( targetMethod, configPath, parameterType );
		if ( method.isAnnotationPresent( ClassInstance.class ) )
			return classInstantiatorSetter( targetMethod, method.getName(), configPath );
		return defaultSetter( targetMethod, configPath );
	}

	Setter listDelegatedSetter( Method method, String configPath, Class<?> parameterType ) {
		return ( instance, config ) -> {
			for ( val confEntry : config.getConfigList( configPath ) ) {
				val value = parse( confEntry, parameterType );
				method.invoke( instance, value );
			}
			return null;
		};
	}

	Setter delegatedSetter( Method method, String configPath, Class<?> targetClass ) {
		return ( instance, config ) -> {
			val value = parse( config.getConfig( configPath ), targetClass );
			return method.invoke( instance, value );
		};
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	Setter enumeratorSetter( Method method, String configPath, Class<?> enumeratorClazz ) {
		return ( instance, config ) -> {
			val value = config.getString( configPath );
			val enumValue = Enum.valueOf( (Class)enumeratorClazz, value );
			return method.invoke( instance, enumValue );
		};
	}

	Setter classInstantiatorSetter( Method method, String methodName, String configPath ) {
		return ( instance, config ) -> {
			val className = config.getString( configPath );
			val type = Class.forName( className );
			val impl = newInstanceOf( type );
			return method.invoke( instance, impl );
		};
	}

	Object newInstanceOf( Class<?> clazz )
			throws ServiceProviderException, InstantiationException, IllegalAccessException
	{
		Object impl = provider.load( clazz );
		if ( impl == null ) {
			impl = clazz.newInstance();
			provider.provideOn( impl );
		}
		return impl;
	}

	Setter defaultSetter( Method method, String configPath ) {
		return ( instance, config ) -> {
			val value = config.getAnyRef( configPath );
			return method.invoke( instance, value );
		};
	}

	interface Setter {
		Object set( Object instance, Config config ) throws Exception;
	}
}