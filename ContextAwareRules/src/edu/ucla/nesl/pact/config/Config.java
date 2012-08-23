package edu.ucla.nesl.pact.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private List<Rule> rules;

    public Config() {
        rules = new ArrayList<Rule>();
    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }

    public List<Rule> getRules() {
        return rules;
    }

}
