package edu.ucla.nesl.pact;

import com.google.gson.*;
import edu.ucla.nesl.pact.config.Config;
import edu.ucla.nesl.pact.config.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RulesParser {
    private String mFunfString;

    public RulesParser() {
        mProbeContexts = new HashSet<String>();
        mProbeContextsByProbeName = new HashMap<String, HashSet<String>>();
        mContextRules = new HashMap<String, HashSet<Rule>>();
    }

    public boolean loadConfigFromFunfConfigJson(String jsonString) {
        JsonParser parser = new JsonParser();

        try {
            JsonElement rootElement = parser.parse(jsonString);
            JsonObject obj = rootElement.getAsJsonObject();
            mFunfString = obj.getAsJsonObject("funf").toString();

            Gson gson = new Gson();
            final Config config =
                    gson.fromJson(obj.get("pact"), Config.class);
            Initialize(config);

        } catch (JsonSyntaxException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public String getFunfConfigString() {
        return mFunfString;
    }

    public void Initialize(Config config) {
        for (Rule rule : config.getRules()) {
            for (List<String> clause : rule.getContexts()) {
                for (String probeContext : clause) {
                    HashSet<Rule> ruleSet = mContextRules.get(probeContext);
                    if (ruleSet == null) {
                        ruleSet = new HashSet<Rule>();
                        mContextRules.put(probeContext,ruleSet);
                    }
                    ruleSet.add(rule);

                }
            }
        }
    }

    // probeContext (atomic contexts) -> set of rules.
    private HashMap<String, HashSet<Rule> > mContextRules;

    public static final int ENTER_EVENT = 1;
    public static final int EXIT_EVENT = 0;


    // Context report is NOT a probeContext.
    // For example: what we get is like:
    //  onContextReceived("ACTIVITY", ENTER_EVENT, "running")
    public void onContextReceived(String probeName, int eventType, String context) {
        final String probeContext = probeName + "." + context;
        checkInit(probeName);
        switch (eventType) {
            case ENTER_EVENT:
                mProbeContextsByProbeName.get(probeName).add(probeContext);
                mProbeContexts.add(probeContext);
                break;
            case EXIT_EVENT:
                mProbeContextsByProbeName.get(probeName).remove(probeContext);
                mProbeContexts.remove(probeContext);
                break;
        }

        if (eventType == ENTER_EVENT) {
            tryExecuteRuleThatMentionsContext(probeContext);
        }
    }

    // Context report is NOT a probeContext.
    // For example: what we get is like:
    //  onContextReceived("ACTIVITY", {"running", "moving"})
    public void onContextReceived(String probeName, HashSet<String> contexts) {
        checkInit(probeName);
        // First remove the previous state from flattened set mProbeContexts
        HashSet<String> previousProbeContexts = mProbeContextsByProbeName.get(probeName);
        if (previousProbeContexts != null) {
            mProbeContexts.removeAll(previousProbeContexts);
        }

        // Prefix everything with probeName to make it a probeContext.
        HashSet<String> probeContexts = new HashSet<String>();
        for (String context : contexts) {
            probeContexts.add(probeName + "." + context);
        }

        // Put the probeContexts into the two state objects we maintain.
        mProbeContextsByProbeName.put(probeName, probeContexts);
        mProbeContexts.addAll(probeContexts);

        tryExecuteRuleThatMentionsContext(probeContexts);

    }

    private void checkInit(String probeName) {
        if (mProbeContextsByProbeName.get(probeName) == null) {
            mProbeContextsByProbeName.put(probeName, new HashSet<String>());
        }
    }

    private void tryExecuteRuleThatMentionsContext (String probeContext) {
        HashSet<Rule> candidateMatches = mContextRules.get(probeContext);
        tryToExecuteRules(candidateMatches);
    }

    private void tryExecuteRuleThatMentionsContext (HashSet<String> probeContexts) {
        HashSet<Rule> candidateMatches = new HashSet<Rule>();
        for (String probeContext : probeContexts) {
            candidateMatches.addAll(mContextRules.get(probeContext));
        }
        tryToExecuteRules(candidateMatches);
    }

    private void tryToExecuteRules(HashSet<Rule> candidateMatches) {
        for (Rule candidateRule: candidateMatches) {
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

    private void doExecuteRule(Rule rule) {
        dump("RUN! ");
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

    // For each probe, track the probeContexts (atomic contexts).
    private HashMap<String, HashSet<String>> mProbeContextsByProbeName;

    // Flattened representation of world state, but probeContext (atomic contexts).
    private HashSet<String> mProbeContexts;
}
