import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.List;

import edu.ucla.nesl.pact.IActionRunner;
import edu.ucla.nesl.pact.IRulesParser;
import edu.ucla.nesl.pact.RulesParser;
import edu.ucla.nesl.pact.config.Action;
import edu.ucla.nesl.pact.config.Rule;
import edu.ucla.nesl.pact.config.RulesConfig;

public class RulesParserTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }


  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  private RulesConfig getSimpleLocationPerturbConfig() {
    RulesConfig rulesConfig = new RulesConfig();

    Rule rule = new Rule("facebook_rule_one",
                         "Perturb location delivered to facebook if in a semantic place.");
    rule.addPackage("com.facebook.katana");
    rule.addContext(new String[]{"location.home", "location.los_angeles"});
    rule.addAction("gps.perturb", "{\"VARIANCE_METERS\": 100}");
    rulesConfig.addRule(rule);
    return rulesConfig;
  }

  public void testRuleParserWithSequentialRule() {
    final RulesConfig rulesConfig = getSimpleLocationPerturbConfig();

    final List<Action> expectedActions = rulesConfig.getRules().get(0).getActions();
    IActionRunner mockRunner = EasyMock.createMock(IActionRunner.class);
    mockRunner.runAction(EasyMock.eq(expectedActions));
    mockRunner.runAction(EasyMock.eq(expectedActions));
    EasyMock.replay(mockRunner);

    IRulesParser parser = new RulesParser();
    parser.Initialize(rulesConfig, mockRunner);
    parser.onContextReceived("location", RulesParser.ENTER_EVENT, "home");
    parser.onContextReceived("location", RulesParser.ENTER_EVENT, "los_angeles");
    parser.onContextReceived("location", RulesParser.EXIT_EVENT, "los_angeles");
    parser.onContextReceived("location", RulesParser.EXIT_EVENT, "los_angeles");
    parser.onContextReceived("location", RulesParser.ENTER_EVENT, "los_angeles");

    EasyMock.verify(mockRunner);
  }
}
