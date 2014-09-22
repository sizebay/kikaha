package kikaha.urouting.converter;

import java.util.Date;
import java.util.GregorianCalendar;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class GregorianCalendarConverter extends AbstractConverter<GregorianCalendar> {

	final DateConverter dateConverter = new DateConverter();

	@Override
	public GregorianCalendar convert( final String value ) throws ConversionException {
		final Date date = dateConverter.convert( value );
		final GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime( date );
		return calendar;
	}
}
