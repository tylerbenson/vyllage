package connections.model.customDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

	private static final String YYYY_MM_DD = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(LocalDateTimeSerializer.class
			.getName());

	private final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern(YYYY_MM_DD);

	@Override
	public void serialize(LocalDateTime date, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {

		if (date == null)
			jgen.writeString("");

		// logger.info(date.toString());

		String dateString = date.format(formatter);

		// logger.info(dateString);

		jgen.writeString(dateString);
	}

}
