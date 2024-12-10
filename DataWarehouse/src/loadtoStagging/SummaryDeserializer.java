package loadtoStagging;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SummaryDeserializer implements JsonDeserializer<Map<String, String>> {
    @Override
    public Map<String, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Map<String, String> summary = new HashMap<>();

        // Nếu là object
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                summary.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        // Nếu là string
        else if (json.isJsonPrimitive()) {
            summary.put("summary", json.getAsString());
        }

        return summary;
    }
}