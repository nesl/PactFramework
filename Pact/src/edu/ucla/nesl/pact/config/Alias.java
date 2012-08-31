package edu.ucla.nesl.pact.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Alias {
  private String name;
  private String description;
  private String aliasType;
  private List<String> values;

  public Alias(){

  }

  public Alias(String name, String description){
    setName(name);
    setDescription(description);
    values = new ArrayList<String>();

  }
  public void addAlias(String member){
    values.add(member);
  }

  public void addAlias(String[] memberArr){
    Collections.addAll(values,memberArr);
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

  public String getAliasType() {
    return aliasType;
  }

  public void setAliasType(String aliasType) {
    this.aliasType = aliasType;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

}
