package kikaha.core.modules.undertow;

import io.undertow.UndertowOptions;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.xnio.Option;
import org.xnio.Options;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class UndertowServerOptionsConfiguration {

	private static Map<String, Option<Boolean>> OPTIONS = new HashMap<>();

	static {
		populateFrom( UndertowOptions.class );
		populateFrom( Options.class );
	}
	
	private static void populateFrom( Class<?> clazz ) {
		for ( final Field field : clazz.getFields() )
			if ( isOptionField(field) ) {
				Option<?> option = retrieveOptionFrom(field);
				populate(option);
			}
	}

	private static void populate(Option option) {
		final String name = option.getName();
		OPTIONS.put(name, option);
	}

	private static Option<?> retrieveOptionFrom(final Field field) {
		try {
			field.setAccessible(true);
			return (Option<?>)field.get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error( "Can't retrieve field value.", e);
			return null;
		}
	}
	
	private static boolean isOptionField( Field field ) {
		return Modifier.isStatic( field.getModifiers() )
			&& Option.class.isAssignableFrom(field.getType());
	}
	
	public static ConfigurableOption getConfigOption( String key, Object value ) {
		Option<?> option = OPTIONS.get(key);
		if ( option != null )
			return new ConfigurableOption(option, value);
		log.warn("No configuration option for " + key);
		return null;
	}

	@Value
	public static class ConfigurableOption {
		@NonNull
		Option option;
		@NonNull Object value;
	}
}
