package documents.model.customDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(LocalDateDeserializer.class
			.getName());

	@Override
	public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		// logger.info("Parsing date " + jp.getText());

		String dateString = jp.getText();

		if (dateString == null || dateString.isEmpty())
			return null;

		// logger.info(dateString);

		LocalDateTime date = LocalDateTime.parse(dateString,
				DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

		return date;
	}
}
