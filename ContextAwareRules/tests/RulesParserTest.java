import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import edu.mit.media.funf.IOUtils;
import edu.ucla.nesl.pact.RulesParser;
import junit.framework.TestCase;

import java.io.*;
import java.nio.charset.Charset;

public class RulesParserTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }



    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private String getStringFromTestJsonFile(String fileName) {
        JsonParser parser = new JsonParser();
        File f = new File("testConfigs/" + fileName);
        try {
            InputStream is = new FileInputStream(f);
            String jsonString = IOUtils.inputStreamToString(is, Charset.defaultCharset().name());
            return jsonString;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void testMalformedJson() {
        RulesParser ruleParser = new RulesParser();
        String jsonString = getStringFromTestJsonFile("default_config.json");
        assertNotNull(jsonString);

        assertFalse(ruleParser.loadConfigFromJson(jsonString));

    }

    public void testSimpleConfig() {
        RulesParser ruleParser = new RulesParser();
        String jsonString = getStringFromTestJsonFile("default_config.json");
        assertNotNull(jsonString);

        assertTrue(ruleParser.loadConfigFromJson(jsonString));
    }
}
