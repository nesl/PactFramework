import com.google.gson.Gson;
import com.google.gson.JsonParser;
import edu.mit.media.funf.IOUtils;
import edu.ucla.nesl.pact.RulesParser;
import edu.ucla.nesl.pact.config.Rule;
import edu.ucla.nesl.pact.config.Config;
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
    /*
    public void testMalformedJson() {
        RulesParser ruleParser = new RulesParser();
        String jsonString = getStringFromTestJsonFile("default_config.json");
        assertNotNull(jsonString);

        assertFalse(ruleParser.loadConfigFromFunfConfigJson(jsonString));

    }

    public void testSimpleConfig() {
        RulesParser ruleParser = new RulesParser();
        String jsonString = getStringFromTestJsonFile("default_config.json");
        assertNotNull(jsonString);

        assertTrue(ruleParser.loadConfigFromFunfConfigJson(jsonString));
    }
    */

    public void testRuleParser() {
        Config config = new Config();

        Rule rule = new Rule("Rule1", "Testing Rule1");
        rule.addContext(new String[] {"location.home", "location.los_angeles"});

        rule.addPackage("com.facebook.katana");
        rule.addAction("gps.perturb", "{\"MEAN\":20, \"VARIANCE\"=30}");
        rule.addAction("acclerometer.suppress", "{}");
        config.addRule(rule);

        RulesParser parser = new RulesParser();
        parser.Initialize(config);

        parser.onContextReceived("location", RulesParser.ENTER_EVENT, "home");
        parser.dump();
        parser.onContextReceived("location", RulesParser.ENTER_EVENT, "los_angeles");
        parser.dump();
        parser.onContextReceived("location", RulesParser.EXIT_EVENT, "los_angeles");
        parser.dump();
        parser.onContextReceived("location", RulesParser.EXIT_EVENT, "los_angeles");
        parser.dump();
        parser.onContextReceived("location", RulesParser.ENTER_EVENT, "los_angeles");
        parser.dump();

        // TODO: Assert a bunch of stuff.
    }
}
