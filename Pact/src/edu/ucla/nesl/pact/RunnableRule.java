package edu.ucla.nesl.pact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import edu.ucla.nesl.pact.config.Action;
import edu.ucla.nesl.pact.config.Rule;

/**
 * Prepares a rule on initialize() so that it can be run() multiple times efficiently.
 *
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public class RunnableRule implements IRunnableRule {

  private Intent mLocationCommandIntent;

  @Override
  public void initialize(Rule rule) {
    mLocationCommandIntent = prepareLocationCommand(rule);
  }

  @Override
  public void run(Context context) {
    context.startService(mLocationCommandIntent);
  }


  protected Intent prepareLocationCommand(Rule rule) {
    Action locationAction = null;
    for (Action action : rule.getActions()) {
      if (action.getName().startsWith("location")) {
        locationAction = action;
        break;
      }
    }

    return (locationAction == null) ? null
                                    : prepareLocationCommand(rule.getPackages(), locationAction);
  }

  protected Intent prepareLocationCommand(List<String> packages, Action action) {
    String command = action.getName();
    String params = action.getParams();
    Intent intent = new Intent("android.override.OverrideCommanderService");
    Bundle data = new Bundle();
    data.putString("COMMAND", command);
    data.putString("PARAMS", params);
    data.putStringArrayList("PACKAGES", new ArrayList<String>(packages));
    return intent;
  }


}
