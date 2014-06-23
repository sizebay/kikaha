package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;

import java.util.Date;
import java.util.GregorianCalendar;

import trip.spi.*;

@Service( AbstractConverter.class )
public class GregorianCalendarConverter extends AbstractConverter<GregorianCalendar> {

	@Provided
	@Name( "date-converter" )
	AbstractConverter<Date> dateConverter;

	@Override
	public GregorianCalendar convert( String value ) throws ConversionException {
		Date date = dateConverter.convert( value );
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime( date );
		return calendar;
	}
}
