package edu.ucla.nesl.pact.config;

import java.util.ArrayList;
import java.util.List;

public class RulesConfig {

  private List<Alias> alias;
  private List<Rule> rules;

  public RulesConfig() {
    alias = new ArrayList<Alias>();
    rules = new ArrayList<Rule>();
  }

  public void addAlias(Alias aliasObj) {
    alias.add(aliasObj);
  }

  public List<Alias> getAlias() {
    return alias;
  }

  public void addRule(Rule rule) {
    rules.add(rule);
  }

  public List<Rule> getRules() {
    return rules;
  }

}