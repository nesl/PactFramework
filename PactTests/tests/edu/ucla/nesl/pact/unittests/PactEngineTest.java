package edu.ucla.nesl.pact.unittests;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import edu.ucla.nesl.pact.IPactEngine;
import edu.ucla.nesl.pact.IRuleScheduler;
import edu.ucla.nesl.pact.PactEngine;
import edu.ucla.nesl.pact.config.Rule;
import edu.ucla.nesl.pact.config.RulesConfig;

/**
 * Unit tests for PactEngine by mocking the RuleScheduler.
 *
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public class PactEngineTest extends TestCase {

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

  public void testPactEngineWithSequentialProbeData() {
    final RulesConfig rulesConfig = getSimpleLocationPerturbConfig();

    final Rule expectedRule = rulesConfig.getRules().get(0);
    IRuleScheduler mockScheduler = EasyMock.createMock(IRuleScheduler.class);
    mockScheduler.scheduleRule(EasyMock.eq(expectedRule));
    mockScheduler.scheduleRule(EasyMock.eq(expectedRule));
    EasyMock.replay(mockScheduler);

    IPactEngine engine = new PactEngine(mockScheduler);

    engine.loadFromConfig(rulesConfig);
    engine.onProbeData("location", PactEngine.ENTER_EVENT, "home");
    engine.onProbeData("location", PactEngine.ENTER_EVENT, "los_angeles");
    engine.onProbeData("location", PactEngine.EXIT_EVENT, "los_angeles");
    engine.onProbeData("location", PactEngine.EXIT_EVENT, "los_angeles");
    engine.onProbeData("location", PactEngine.ENTER_EVENT, "los_angeles");

    EasyMock.verify(mockScheduler);
  }

  public void testPactEngineWithStateProbeData() {
    // TODO: Need to write a test case for the other type of probe data.
  }
}
