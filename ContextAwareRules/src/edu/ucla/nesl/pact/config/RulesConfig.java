package edu.ucla.nesl.pact.config;

import java.util.ArrayList;
import java.util.List;

public class RulesConfig {

  private List<Rule> rules;

  public RulesConfig() {
    rules = new ArrayList<Rule>();
  }

  public void addRule(Rule rule) {
    rules.add(rule);
  }

  public List<Rule> getRules() {
    return rules;
  }

}
