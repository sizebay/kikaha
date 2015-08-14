package kikaha.core.impl;

import io.undertow.UndertowOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import org.xnio.Option;
import org.xnio.Options;

import com.typesafe.config.ConfigValue;

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
	
	public static ConfigurableOption getConfigOption( ConfigValue config, String key ) {
		Option<?> option = OPTIONS.get(key);
		if ( option != null )
			return parse(config, option);
		log.warn("No configuration option for " + key);
		return null;
	}

	private static ConfigurableOption parse( ConfigValue config, Option<?> option ) {
		final Object value = config.unwrapped();
		log.info( "  > found :" + option + ": " + value );
		return new ConfigurableOption(option, value);
	}

	@Value
	public static class ConfigurableOption {
		@NonNull Option option;
		@NonNull Object value;
	}
}
