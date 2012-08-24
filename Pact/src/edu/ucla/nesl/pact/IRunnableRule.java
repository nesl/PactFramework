package edu.ucla.nesl.pact;

import android.content.Context;

import edu.ucla.nesl.pact.config.Rule;

/**
 * TODO: Give a one line description.
 *
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public interface IRunnableRule {

  public void initialize(Rule rule);

  public void run(Context context);

}
