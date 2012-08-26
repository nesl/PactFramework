package edu.ucla.nesl.pact;

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

  // probeContext (atomic contexts) -> set of rules.
  private HashMap<String, HashSet<Rule>> mContextRules;

  // For each probe, track the probeContexts (atomic contexts).
  private HashMap<String, HashSet<String>> mProbeContextsByProbeName;

  // Flattened representation of world state, but probeContext (atomic contexts).
  private HashSet<String> mProbeContexts;

  private boolean mInitialized;

  public static final int ENTER_EVENT = 1;
  public static final int EXIT_EVENT = 0;

  public PactEngine(IRuleScheduler ruleScheduler) {
    mRuleScheduler = ruleScheduler;
    mInitialized = false;
  }

  @Override
  public void loadFromConfig(RulesConfig rulesConfig) {
    mProbeContexts = new HashSet<String>();
    mProbeContextsByProbeName = new HashMap<String, HashSet<String>>();
    mContextRules = new HashMap<String, HashSet<Rule>>();
    for (Rule rule : rulesConfig.getRules()) {
      for (List<String> clause : rule.getContexts()) {
        for (String probeContext : clause) {
          HashSet<Rule> ruleSet = mContextRules.get(probeContext);
          if (ruleSet == null) {
            ruleSet = new HashSet<Rule>();
            mContextRules.put(probeContext, ruleSet);
          }
          ruleSet.add(rule);

        }
      }
    }

    mInitialized = true;
  }


  /**
   * Process data from a probe that reports changes its state.
   *
   * @param eventType ENTER_EVENT or EXIT_EVENT.
   */
  @Override
  public void onProbeData(String probeName, int eventType, String state) {
    final String probeState = probeName + "." + state;
    if (!mInitialized) {
      return;
    }

    initializeProbe(probeName);
    switch (eventType) {
      case ENTER_EVENT:
        mProbeContextsByProbeName.get(probeName).add(probeState);
        mProbeContexts.add(probeState);
        break;
      case EXIT_EVENT:
        mProbeContextsByProbeName.get(probeName).remove(probeState);
        mProbeContexts.remove(probeState);
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
  public void onProbeData(String probeName, HashSet<String> states) {
    if (!mInitialized) {
      return;
    }

    initializeProbe(probeName);
    // First remove the previous state from flattened set mProbeContexts
    HashSet<String> previousProbeContexts = mProbeContextsByProbeName.get(probeName);
    if (previousProbeContexts != null) {
      mProbeContexts.removeAll(previousProbeContexts);
    }

    // Prefix everything with probeName to make it a probeContext.
    HashSet<String> probeStates = new HashSet<String>();
    for (String context : states) {
      probeStates.add(probeName + "." + context);
    }

    // Put the probeContexts into the two state objects we maintain.
    mProbeContextsByProbeName.put(probeName, probeStates);
    mProbeContexts.addAll(probeStates);

    doExecuteRulesThatMentionProbeState(probeStates);

  }

  private void initializeProbe(String probeName) {
    if (mProbeContextsByProbeName.get(probeName) == null) {
      mProbeContextsByProbeName.put(probeName, new HashSet<String>());
    }
  }

  protected void doExecuteRulesThatMentionProbeState(String probeContext) {
    HashSet<Rule> candidateMatches = mContextRules.get(probeContext);
    if (candidateMatches != null)
      doExecuteRuleIfSatisfied(candidateMatches);
  }

  protected void doExecuteRulesThatMentionProbeState(HashSet<String> probeContexts) {
    HashSet<Rule> candidateMatches = new HashSet<Rule>();
    for (String probeContext : probeContexts) {
      HashSet<Rule> matches = mContextRules.get(probeContext);
      if (matches != null)
        candidateMatches.addAll(matches);
    }
    doExecuteRuleIfSatisfied(candidateMatches);
  }

  protected void doExecuteRuleIfSatisfied(HashSet<Rule> candidateMatches) {
    for (Rule candidateRule : candidateMatches) {
      for (List<String> clause : candidateRule.getContexts()) {
        // TODO: Push this inside the initialization of the rule.
        HashSet<String> clause_set = new HashSet<String>(clause);
        final int clause_size = clause_set.size();
        clause_set.retainAll(mProbeContexts);
        final int final_clause_size = clause_set.size();

        if (clause_size == final_clause_size) {
          doExecuteRule(candidateRule);
        }
      }
    }
  }

  public void doExecuteRule(Rule rule) {
    mRuleScheduler.scheduleRule(rule);
  }

  public void dump() {
    dump("DUMP: ");
  }

  public void dump(String prefix) {
    StringBuilder builder = new StringBuilder();
    builder.append(prefix);

    builder.append(" Context = {");
    for (String c : mProbeContexts) {
      builder.append(c);
      builder.append(",");
    }
    builder.append("}");
    System.out.println(builder.toString());
  }
}
