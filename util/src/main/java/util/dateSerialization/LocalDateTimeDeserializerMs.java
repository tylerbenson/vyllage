package util.dateSerialization;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateTimeDeserializerMs extends
		JsonDeserializer<LocalDateTime> {

	private static final String YYYY_MM_DD = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(LocalDateTimeDeserializerMs.class.getName());

	@Override
	public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		// logger.info("Parsing date " + jp.getText());

		String dateString = jp.getText();

		if (dateString == null || dateString.isEmpty())
			return null;

		// logger.info(dateString);

		LocalDateTime date = LocalDateTime.parse(dateString,
				DateTimeFormatter.ofPattern(YYYY_MM_DD));

		return date;
	}

}
