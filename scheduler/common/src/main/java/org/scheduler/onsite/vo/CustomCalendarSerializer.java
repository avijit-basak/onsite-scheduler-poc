package org.scheduler.onsite.vo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomCalendarSerializer extends JsonSerializer<Calendar> {

	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final Locale LOCALE_HUNGARIAN = new Locale("hu", "HU");
	public static final TimeZone LOCAL_TIME_ZONE = TimeZone.getTimeZone("Europe/Budapest");

	@Override
	public void serialize(Calendar value, JsonGenerator gen, SerializerProvider arg2)
			throws IOException, JsonProcessingException {
		if (value == null) {
			gen.writeNull();
		} else {
			gen.writeString(FORMATTER.format(value.getTime()));
		}
	}
}
