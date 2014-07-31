package kikaha.urouting.converter;

import java.util.Date;
import java.util.GregorianCalendar;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class GregorianCalendarConverter extends AbstractConverter<GregorianCalendar> {

	@Provided( name = "date-converter" )
	AbstractConverter<Date> dateConverter;

	@Override
	public GregorianCalendar convert( String value ) throws ConversionException {
		Date date = dateConverter.convert( value );
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime( date );
		return calendar;
	}
}
