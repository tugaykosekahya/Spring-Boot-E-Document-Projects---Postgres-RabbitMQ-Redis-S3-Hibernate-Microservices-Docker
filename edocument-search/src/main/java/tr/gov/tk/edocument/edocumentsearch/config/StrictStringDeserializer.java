package tr.gov.gib.evdbelge.evdbelgesorgulama.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

public class StrictStringDeserializer extends StringDeserializer {
    //Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.currentToken();
        if (token.isBoolean()
                || token.isNumeric()
                || !token.toString().equalsIgnoreCase("VALUE_STRING")) {
            //LOGGER.error(p.getCurrentName() + " : " + p.getValueAsString() + " -> girilen değer String olmalıdır.");
            ctxt.reportInputMismatch(String.class, "%s : %s -> girilen değer String olmalıdır.", p.getCurrentName(), p.getValueAsString());
            return null;
        }
        return super.deserialize(p, ctxt);
    }
}
