package edu.ucla.nesl.pact;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class RulesParser {
    private String mFunfString;

    public boolean loadConfigFromJson(String jsonString) {
        JsonParser parser = new JsonParser();

        try {
            JsonElement rootElement = parser.parse(jsonString);
            JsonObject obj = rootElement.getAsJsonObject();
            mFunfString = obj.getAsJsonObject("funf").toString();
            String pactObj = obj.getAsJsonObject("pact").toString();

            // TODO: PARSE THIS INTO A DATA STRUCTURE.

        } catch (JsonSyntaxException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public String getFunfConfigString() {
        return mFunfString;
    }


}
