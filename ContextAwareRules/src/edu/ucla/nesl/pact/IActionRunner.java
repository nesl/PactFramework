package edu.ucla.nesl.pact;

import java.util.List;

import edu.ucla.nesl.pact.config.Action;

/**
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public interface IActionRunner {

  public void runAction(List<Action> actions);
}
