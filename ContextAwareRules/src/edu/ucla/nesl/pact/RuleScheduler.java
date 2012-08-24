package edu.ucla.nesl.pact;

import android.content.Context;

import edu.ucla.nesl.pact.config.Rule;

/**
 * Schedules rules so that they be run efficiently.
 *
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public class RuleScheduler implements IRuleScheduler {

  Context mAndroidContext;

  public RuleScheduler(Context androidContext) {
    mAndroidContext = androidContext;
  }

  @Override
  public void scheduleRule(Rule rule) {
    // TODO: Parse into runnable rules once, to avoid remaking it every time it is executed.
    IRunnableRule runnableRule = new RunnableRule();
    runnableRule.initialize(rule);
    runnableRule.run(mAndroidContext);
  }
}
