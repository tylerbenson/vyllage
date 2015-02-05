package profile.model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate>  {
	
	Logger logger = Logger.getLogger(LocalDateDeserializer.class.getName());
	
	@Override
	public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		logger.info("Parsing date " + jp.getText());
		
        String dateString = jp.getText();
        
        if(dateString == null || dateString.isEmpty())
        	return null;
        dateString += " 01"; //TODO: without the dd part it cannot be parsed into a valid date.
        
        logger.info(dateString);
        
        LocalDate date = LocalDate.parse(dateString,
				DateTimeFormatter.ofPattern("MMMM yyyy dd"));
        
        return date;
	}

}
