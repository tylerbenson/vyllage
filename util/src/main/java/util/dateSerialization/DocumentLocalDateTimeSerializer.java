package util.dateSerialization;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DocumentLocalDateTimeSerializer extends
		JsonSerializer<LocalDateTime> {

	private static final String YYYY_MM_DD = "yyyy-MM-dd'T'HH:mm:ss";

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(DocumentLocalDateTimeSerializer.class.getName());

	private final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern(YYYY_MM_DD);

	@Override
	public void serialize(LocalDateTime date, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {

		String dateString = "";

		if (date != null)
			dateString = date.format(formatter);

		jgen.writeString(dateString);
	}

}
