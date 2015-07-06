package kikaha.urouting.converter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import trip.spi.Singleton;
import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;

@Singleton( exposedAs=AbstractConverter.class, name="zoned-date-time-converter" )
public class ZonedDateTimeConverter extends AbstractConverter<ZonedDateTime> {

	final DateConverter converter = new DateConverter();

	@Override
	public ZonedDateTime convert(String dataAsStr) throws ConversionException {
		final Date date = converter.convert( dataAsStr );
		return ZonedDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() );
	}
}
