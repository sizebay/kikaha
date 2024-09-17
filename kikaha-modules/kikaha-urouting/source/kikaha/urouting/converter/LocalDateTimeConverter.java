package kikaha.urouting.converter;

import kikaha.core.util.Lang;
import kikaha.urouting.api.AbstractConverter;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
@Typed( AbstractConverter.class )
public class LocalDateTimeConverter extends AbstractConverter<LocalDateTime> {

	@Override
	public LocalDateTime convert(String dateAsString) {
		if (Lang.isUndefined(dateAsString) )
			return null;
		return  LocalDateTime.parse( dateAsString, DateTimeFormatter.ISO_LOCAL_DATE_TIME );
	}

}
