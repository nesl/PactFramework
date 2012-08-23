import junit.framework.TestCase;

import edu.ucla.nesl.pact.RulesParser;
import edu.ucla.nesl.pact.config.Config;
import edu.ucla.nesl.pact.config.Rule;

public class RulesParserTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }


  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testRuleParser() {
    Config config = new Config();

    Rule rule = new Rule("Rule1", "Testing Rule1");
    rule.addContext(new String[]{"location.home", "location.los_angeles"});

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
