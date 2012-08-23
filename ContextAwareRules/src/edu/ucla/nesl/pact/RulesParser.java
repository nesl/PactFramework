package edu.ucla.nesl.pact;

import com.google.gson.*;
import edu.ucla.nesl.pact.config.Config;

public class RulesParser {
    private String mFunfString;

    public boolean loadConfigFromFunfConfigJson(String jsonString) {
        JsonParser parser = new JsonParser();

        try {
            JsonElement rootElement = parser.parse(jsonString);
            JsonObject obj = rootElement.getAsJsonObject();
            mFunfString = obj.getAsJsonObject("funf").toString();

            Gson gson = new Gson();
            final Config config =
                    gson.fromJson(obj.get("pact"), Config.class);
            Initialize(config);

        } catch (JsonSyntaxException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public String getFunfConfigString() {
        return mFunfString;
    }

    public void Initialize(Config config) {

    }



}
