package edu.ucla.nesl.pact;

import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
import android.test.ServiceTestCase;

import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.HashSet;

import edu.mit.media.funf.probe.Probe;
import edu.ucla.nesl.funf.NearbyPlacesProbe;
import edu.ucla.nesl.pact.config.Rule;
import edu.ucla.nesl.pact.config.RulesConfig;

/**
 * TODO: Give a one line description.
 *
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public class PactServiceTest extends ServiceTestCase<PactService> {

  public PactServiceTest() {
    super(PactService.class);
  }

  public void testActionReportPlacesData() {
    final String probeName = NearbyPlacesProbe.class.getName();
    ArrayList<String> places = new ArrayList<String>();
    places.add("home");
    places.add("los_angeles");

    // Setup expectation.
    HashSet<String> placesSet = new HashSet<String>(places);
    IPactEngine mockEngine = EasyMock.createMock(IPactEngine.class);
    mockEngine.onProbeData(EasyMock.eq(probeName), EasyMock.eq(placesSet));
    EasyMock.replay(mockEngine);

    // Test it.
    startService(new Intent());
    getService().setEngine(mockEngine);

    Bundle bundle = new Bundle();
    bundle.putString(Probe.PROBE, NearbyPlacesProbe.class.getName());
    bundle.putLong(Probe.TIMESTAMP, System.currentTimeMillis());
    bundle.putStringArrayList(NearbyPlacesProbe.PLACES, places);
    Intent intent = new Intent(getContext(), PactService.class);
    intent.setAction(PactService.ACTION_REPORT_DATA);
    intent.putExtras(bundle);
    startService(intent);

    // Verify it.
    EasyMock.verify(mockEngine);

  }

  public void testActionUpdateConfig() {
    final RulesConfig rulesConfig = getSimpleLocationPerturbConfig();
    final String jsonConfig = new Gson().toJson(rulesConfig);

    // Setup expectation.
    IPactEngine mockEngine = EasyMock.createMock(IPactEngine.class);
    mockEngine.loadFromConfig(EasyMock.anyObject(RulesConfig.class));
    EasyMock.replay(mockEngine);

    // Test it.
    startService(new Intent());
    getService().setEngine(mockEngine);

    Bundle bundle = new Bundle();
    bundle.putString(PactService.JSON_CONFIG, jsonConfig);
    Intent intent = new Intent(getContext(), PactService.class);
    intent.setAction(PactService.ACTION_UPDATE_CONFIG);
    intent.putExtras(bundle);
    startService(intent);

    // Verify it.
    EasyMock.verify(mockEngine);

  }

  private RulesConfig getSimpleLocationPerturbConfig() {
    RulesConfig rulesConfig = new RulesConfig();

    Rule rule = new Rule("facebook_rule_one",
                         "Perturb location delivered to facebook if in a semantic place.");
    rule.addPackage("com.facebook.katana");
    rule.addContext(new String[]{"location.home", "location.los_angeles"});
    rule.addAction("gps.perturb", "{\"VARIANCE_METERS\": 100}");
    rulesConfig.addRule(rule);
    return rulesConfig;
  }

}
