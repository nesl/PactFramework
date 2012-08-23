package edu.ucla.nesl.pact.config;
public class Action {
    private String name;
    private String params;

    public Action() { }

    public Action(String name, String params) {
         setName(name);
         setParams(params);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
