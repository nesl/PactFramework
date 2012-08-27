package edu.ucla.nesl.pact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.ucla.nesl.pact.config.Rule;
import edu.ucla.nesl.pact.config.RulesConfig;

/**
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public class PactEngine implements IPactEngine {

  private IRuleScheduler mRuleScheduler;

  // probeContext (atomic contexts) -> set of rules. e.g. los_angles is an atomic context
    // mMapStateToRules
  private HashMap<String, HashSet<Rule>> mMapStateToRules;

  // For each probe, track the probeState (probeName.atomic contexts).
  private HashMap<String, HashSet<String>> mMapProbeNameToProbeState;

  // Flattened representation of world state, but probeState (probeName.atomic context).
  private HashSet<String> mProbeStates;

  private boolean mInitialized;

  public static final int ENTER_EVENT = 1;
  public static final int EXIT_EVENT = 0;

  public PactEngine(IRuleScheduler ruleScheduler) {
    mRuleScheduler = ruleScheduler;
    mInitialized = false;
    mProbeStates = new HashSet<String>();
    mMapProbeNameToProbeState = new HashMap<String, HashSet<String>>();
    mMapStateToRules = new HashMap<String, HashSet<Rule>>();
  }

  @Override
  public void loadFromConfig(RulesConfig rulesConfig) {
    for (Rule rule : rulesConfig.getRules()) {
      for (List<String> clause : rule.getContexts()) {
        for (String probeState : clause) {
          HashSet<Rule> ruleSet = mMapStateToRules.get(probeState);
          if (ruleSet == null) {
            ruleSet = new HashSet<Rule>();
            mMapStateToRules.put(probeState, ruleSet);
          }
          ruleSet.add(rule);
        }
      }
    }
    mInitialized = true;
  }

  /**
   * Process data from a probe that reports changes its probeContext.
   *
   * @param eventType ENTER_EVENT or EXIT_EVENT.
   */
  @Override
  public void onProbeData(String probeName, int eventType, String probeContext) {
    final String probeState = probeName + "." + probeContext;
      // check if config data has been loaded
    if (!mInitialized) {
      return;
    }
    initializeProbe(probeName);
    switch (eventType) {
      case ENTER_EVENT:
        mMapProbeNameToProbeState.get(probeName).add(probeState);
        mProbeStates.add(probeState);
        break;
      case EXIT_EVENT:
        mMapProbeNameToProbeState.get(probeName).remove(probeState);
        mProbeStates.remove(probeState);
        break;
    }
    if (eventType == ENTER_EVENT) {
      doExecuteRulesThatMentionProbeState(probeState);
    }
  }

  /**
   * Process data from a probe that reports it's full set of state.
   */
  @Override
  public void onProbeData(String probeName, HashSet<String> contexts) {
    if (!mInitialized) {
      return;
    }
    initializeProbe(probeName);
    // First remove the previous state from flattened set mProbeStates
    HashSet<String> previousProbeStates = mMapProbeNameToProbeState.get(probeName);
    if (previousProbeStates != null) {
      mProbeStates.removeAll(previousProbeStates);
    }
    // Prefix everything with probeName to make it a probeContext.
    HashSet<String> probeStates = new HashSet<String>();
    for (String context : contexts) {
      probeStates.add(probeName + "." + context);
    }
    // Put the probeContexts into the two state objects we maintain.
    mMapProbeNameToProbeState.put(probeName, probeStates);
    mProbeStates.addAll(probeStates);

    doExecuteRulesThatMentionProbeState(probeStates);
  }

  private void initializeProbe(String probeName) {
    if (mMapProbeNameToProbeState.get(probeName) == null) {
      mMapProbeNameToProbeState.put(probeName, new HashSet<String>());
    }
  }

  protected void doExecuteRulesThatMentionProbeState(String probeContext) {
    HashSet<Rule> candidateMatches = mMapStateToRules.get(probeContext);
    if (candidateMatches != null)
      doExecuteRuleIfSatisfied(candidateMatches);
  }

  protected void doExecuteRulesThatMentionProbeState(HashSet<String> probeStates) {
    HashSet<Rule> candidateMatches = new HashSet<Rule>();
    for (String probeState : probeStates) {
      HashSet<Rule> matches = mMapStateToRules.get(probeState);
      if (matches != null)
        candidateMatches.addAll(matches);
    }
    doExecuteRuleIfSatisfied(candidateMatches);
  }

  protected void doExecuteRuleIfSatisfied(HashSet<Rule> candidateMatches) {
      ArrayList<Rule> mRulesToExecute = new ArrayList<Rule>();
    for (Rule candidateRule : candidateMatches) {
      for (List<String> clause : candidateRule.getContexts()) {
        // TODO: Push this inside the initialization of the rule.
        HashSet<String> clause_set = new HashSet<String>(clause);
        final int clause_size = clause_set.size();
          //retain elements in clause_set which matches those in mProbeStates
        clause_set.retainAll(mProbeStates);
        final int final_clause_size = clause_set.size();

        if (clause_size == final_clause_size) {
            mRulesToExecute.add(candidateRule);
          //doExecuteRule(candidateRule);
        }
      }
    }
    doExecuteRule(mRulesToExecute);
  }

  public void doExecuteRule(Rule rule) {
    mRuleScheduler.scheduleRule(rule);
  }

  public void doExecuteRule(ArrayList<Rule> rulesToExecute) {
    mRuleScheduler.scheduleRule(rulesToExecute);
  }

  public void dump() {
    dump("DUMP: ");
  }

  public void dump(String prefix) {
    StringBuilder builder = new StringBuilder();
    builder.append(prefix);

    builder.append(" Context = {");
    for (String c : mProbeStates) {
      builder.append(c);
      builder.append(",");
    }
    builder.append("}");
    System.out.println(builder.toString());
  }
}
