package edu.ucla.nesl.pact.config;

import java.util.ArrayList;
import java.util.List;

public class Rule {

  private String name;
  private String description;
  private List<List<String>> contexts;
  private List<String> packages;
  private List<Action> actions;

  public Rule() {

  }

  public Rule(String name, String description) {
    setName(name);
    setDescription(description);
    setContexts(new ArrayList<List<String>>());
    setPackages(new ArrayList<String>());
    setActions(new ArrayList<Action>());
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<List<String>> getContexts() {
    return contexts;
  }

  public void setContexts(List<List<String>> contexts) {
    this.contexts = contexts;
  }

  public List<String> getPackages() {
    return packages;
  }

  public void setPackages(List<String> packages) {
    this.packages = packages;
  }

  public List<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }

  public void addPackage(String pkgName) {
    packages.add(pkgName);
  }

  public void addAction(String actionName, String paramStr) {

    actions.add(new Action(actionName, paramStr));
  }

  public void addContext(String context) {
    List<String> clause = new ArrayList<String>();
    clause.add(context);
    contexts.add(clause);
  }

  public void addContext(String[] clause_arr) {
    List<String> clause = new ArrayList<String>();

    for (String c : clause_arr) {
      clause.add(c);
    }

    contexts.add(clause);
  }

}
