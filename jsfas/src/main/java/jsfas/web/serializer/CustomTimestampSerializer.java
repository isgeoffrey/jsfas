package jsfas.web.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomTimestampSerializer extends JsonSerializer<Object> {
	
	@Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider arg2) throws 
        IOException, JsonProcessingException {      

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        String formattedDate = formatter.format(value);

        gen.writeString(formattedDate);

    }	
	
}