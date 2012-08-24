package edu.ucla.nesl.pact;

import java.util.HashSet;

import edu.ucla.nesl.pact.config.Rule;
import edu.ucla.nesl.pact.config.RulesConfig;

/**
 * TODO: Give a one line description.
 *
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public interface IPactEngine {

  public void loadFromConfig(RulesConfig rulesConfig);

  public void onProbeData(String probeName, int eventType, String context);

  public void onProbeData(String probeName, HashSet<String> contexts);

  public void doExecuteRule(Rule rule);

}
