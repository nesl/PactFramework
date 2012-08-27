package edu.ucla.nesl.pact;

import com.google.gson.Gson;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.HashSet;

import edu.mit.media.funf.probe.Probe;
import edu.ucla.nesl.funf.NearbyPlacesProbe;
import edu.ucla.nesl.pact.config.RulesConfig;

/**
 * TODO: Give a one line description.
 *
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public class PactService extends Service {

  private static final String TAG = "PactService";

  public static final String ACTION_PROBE_DATA = "edu.ucla.nesl.pact.ACTION_PROBE_DATA";
  public static final String ACTION_CONFIG_UPDATE = "edu.ucla.nesl.pact.ACTION_CONFIG_UPDATE";

  public static final String JSON_CONFIG = "JSON_CONFIG";

  private IPactEngine mPactEngine;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


  @Override
  public void onCreate() {
    initializeOnce(new PactEngine(new RuleScheduler(this)));
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    final String action = intent.getAction();
    final Bundle data = intent.getExtras();
    if (action != null && data != null) {
      if (action.equals(ACTION_CONFIG_UPDATE)) {
        onUpdateConfig(data);
      } else if (action.equals(ACTION_PROBE_DATA)) {
        onProbeData(data);
      }
    }
    return super.onStartCommand(intent, flags, startId);
  }

  public void setEngine(IPactEngine engine) {
    mPactEngine = engine;
  }

  // Useful for testing. If the engine is mocked, then onCreate does not overwrite it.
  public void initializeOnce(IPactEngine engine) {
    if (mPactEngine == null) {
      mPactEngine = engine;
    }
  }

  protected void onUpdateConfig(Bundle data) {
    onUpdateConfig(data.getString(JSON_CONFIG, ""));
  }

  protected void onUpdateConfig(String jsonConfig) {
    Gson gson = new Gson();
    final RulesConfig rulesConfig =
        gson.fromJson(jsonConfig, RulesConfig.class);
    mPactEngine.loadFromConfig(rulesConfig);
  }

  protected void onProbeData(Bundle data) {
    final String probeName = data.getString(Probe.PROBE, "");
    final String placesProbeName = NearbyPlacesProbe.class.getName();

    if (probeName.equals(placesProbeName)) {
      final Integer event = data.getInt("EVENT", -1);
      if (event == -1) {
        // The probe data doesn't contain an Event, sot it must be a state reporting probe.
        // TODO: Maybe it's more efficient to communicate via ArrayList ?
        final ArrayList<String> stateArr = data.getStringArrayList(NearbyPlacesProbe.PLACES);
        if (stateArr == null) {
          return;
        }
        HashSet<String> stateSet = new HashSet<String>(stateArr);
        mPactEngine.onProbeData(probeName, stateSet);
      } else {
        final String place = data.getString(NearbyPlacesProbe.PLACES);
        if (place == null) {
          return;
        }
        mPactEngine.onProbeData(probeName, event, place);
      }
    }
  }
}
