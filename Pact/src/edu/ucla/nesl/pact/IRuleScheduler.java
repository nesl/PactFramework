package edu.ucla.nesl.pact;

import edu.ucla.nesl.pact.config.Rule;

import java.util.ArrayList;

/**
 * TODO: Give a one line description.
 *
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public interface IRuleScheduler {

  public void scheduleRule(Rule rule);
  public void scheduleRule(ArrayList<Rule> rules);

}
