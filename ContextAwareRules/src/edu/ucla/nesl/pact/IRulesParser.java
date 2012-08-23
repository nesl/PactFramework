package edu.ucla.nesl.pact;

import java.util.HashSet;

import edu.ucla.nesl.pact.config.RulesConfig;

/**
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public interface IRulesParser {

  public void Initialize(RulesConfig rulesConfig, IActionRunner runner);

  public void onContextReceived(String probeName, int eventType, String context);

  public void onContextReceived(String probeName, HashSet<String> contexts);

}
