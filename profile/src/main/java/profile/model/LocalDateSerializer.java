package profile.model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LocalDateSerializer extends JsonSerializer<LocalDate> {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(LocalDateSerializer.class.getName());
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
	
	@Override
	public void serialize(LocalDate date, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		
		if(date == null)
			jgen.writeString("");
		
		//logger.info(date.toString());
		
		String dateString =  date.format(formatter);
		
		//logger.info(dateString);
		
		jgen.writeString(dateString);
	}

}
