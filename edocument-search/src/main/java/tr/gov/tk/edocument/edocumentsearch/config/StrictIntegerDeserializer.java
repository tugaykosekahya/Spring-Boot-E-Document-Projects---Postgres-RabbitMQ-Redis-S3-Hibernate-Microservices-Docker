package tr.gov.gib.evdbelge.evdbelgesorgulama.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;

import java.io.IOException;

public class StrictIntegerDeserializer extends NumberDeserializers.NumberDeserializer {
    //Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.currentToken();
        if (token.isBoolean()
                || !token.toString().equalsIgnoreCase("VALUE_NUMBER_INT")) {
            //LOGGER.error(p.getCurrentName() + " : " + p.getValueAsString() + " -> girilen değer Integer olmalıdır.");
            ctxt.reportInputMismatch(Integer.class, "%s : %s -> girilen değer Integer olmalıdır.", p.getCurrentName(), p.getValueAsString());
            return null;
        }
        return super.deserialize(p, ctxt);
    }
}
