package kikaha.urouting.api;

import java.util.HashMap;
import java.util.Map;

import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

@Singleton
@SuppressWarnings( "rawtypes" )
public class ConverterFactory {

	Map<String, Class<? extends AbstractConverter<?>>> converters;

	@Provided
	ServiceProvider provider;

	public Map<String, Class<? extends AbstractConverter<?>>> getConverters() {
		if ( converters == null ) {
			converters = new HashMap<String, Class<? extends AbstractConverter<?>>>();
			loadAllConverters();
		}
		return converters;
	}

	public void loadAllConverters() {
		try {
			final Iterable<AbstractConverter> converters = provider.loadAll( AbstractConverter.class );
			register( converters );
		} catch ( ServiceProviderException cause ) {
			throw new RuntimeException( cause );
		}
	}

	public void register( Iterable<AbstractConverter> converters ) {
		for ( AbstractConverter converter : converters )
			register( converter );
	}

	@SuppressWarnings( "unchecked" )
	public void register( AbstractConverter<?> newInstance ) {
		register( newInstance.getGenericClass(),
				(Class<? extends AbstractConverter<?>>)newInstance.getClass() );
	}

	public void register( Class<?> targetClass, Class<? extends AbstractConverter<?>> converter ) {
		this.getConverters().put( targetClass.getCanonicalName(), converter );
	}

	@SuppressWarnings( "unchecked" )
	public <T> T decode( String value, Class<T> clazz ) throws ConversionException {
		try {
			if ( value == null || String.class.equals( clazz ) )
				return (T)value;
			AbstractConverter<T> converter = getConverterFor( clazz );
			return converter.convert( value );
		} catch ( InstantiationException | IllegalAccessException e ) {
			throw new ConversionException( String.format(
					"Can't convert '%s' to '%s'", value, clazz.getCanonicalName() ), e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public <T> AbstractConverter<T> getConverterFor( Class<T> clazz )
			throws InstantiationException, IllegalAccessException, ConversionException {
		String canonicalName = clazz.getCanonicalName();
		Class<? extends AbstractConverter<T>> converterClass =
				(Class<? extends AbstractConverter<T>>)this.getConverters().get( canonicalName );
		if ( converterClass == null )
			throw new ConversionException( "No converters defined to " + canonicalName );
		return converterClass.newInstance();
	}

}
