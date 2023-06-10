package org.scheduler.onsite.request.vo;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomCalendarDeserializer extends JsonDeserializer<Calendar> {
	@Override
	public Calendar deserialize(JsonParser jsonparser, DeserializationContext context)
			throws IOException, JsonProcessingException {
		String dateAsString = jsonparser.getText();
		try {
			Date date = CustomCalendarSerializer.FORMATTER.parse(dateAsString);
			Calendar calendar = Calendar.getInstance(CustomCalendarSerializer.LOCAL_TIME_ZONE);
			calendar.setTime(date);
			return calendar;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
