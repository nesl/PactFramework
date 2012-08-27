package edu.ucla.nesl.funf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import edu.mit.media.funf.IOUtils;
import edu.mit.media.funf.Utils;
import edu.mit.media.funf.configured.ConfiguredPipeline;
import edu.mit.media.funf.configured.FunfConfig;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.storage.BundleSerializer;
import edu.ucla.nesl.pact.IPactEngine;
import edu.ucla.nesl.pact.PactService;

import static edu.mit.media.funf.AsyncSharedPrefs.async;


public class MainPipeline extends ConfiguredPipeline {

  public static final String TAG = "FunfBGCollector";
  public static final String MAIN_CONFIG = "main_config";
  public static final String START_DATE_KEY = "START_DATE";

  public static final String ACTION_RUN_ONCE = "RUN_ONCE";
  public static final String RUN_ONCE_PROBE_NAME = "PROBE_NAME";


  IPactEngine mPactEngine = null;

  @Override
  public void onCreate() {
    super.onCreate();
    sendConfigToPactService(getConfig().getPactConfigJson());
  }

  @Override
  protected void onConfigChange(String json) {
    super.onConfigChange(json);
    sendConfigToPactService(getConfig().getPactConfigJson());
  }

  @Override
  public void reload() {
    super.reload();
    final Context context = this;
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (ACTION_RUN_ONCE.equals(intent.getAction())) {
      String probeName = intent.getStringExtra(RUN_ONCE_PROBE_NAME);
      runProbeOnceNow(probeName);
    } else {
      super.onHandleIntent(intent);
    }
  }

  @Override
  public BundleSerializer getBundleSerializer() {
    return new BundleToJson();
  }

  public static class BundleToJson implements BundleSerializer {

    public String serialize(Bundle bundle) {
      return JsonUtils.getGson().toJson(Utils.getValues(bundle));
    }

  }

  @Override
  public void onDataReceived(Bundle data) {
    super.onDataReceived(data);
    sendDataToPactService(data);
  }


  @Override
  public void onStatusReceived(Probe.Status status) {
    super.onStatusReceived(status);
    // Fill this in with extra behaviors on status received
  }

  @Override
  public void onDetailsReceived(Probe.Details details) {
    super.onDetailsReceived(details);
    // Fill this in with extra behaviors on details received
  }

  public static boolean isEnabled(Context context) {
    return getSystemPrefs(context).getBoolean(ENABLED_KEY, true);
  }

  @Override
  public SharedPreferences getSystemPrefs() {
    return getSystemPrefs(this);
  }

  public static SharedPreferences getSystemPrefs(Context context) {
    return async(
        context.getSharedPreferences(MainPipeline.class.getName() + "_system", MODE_PRIVATE));
  }

  @Override
  public FunfConfig getConfig() {
    return getMainConfigAlwaysReload(this);
  }

  /**
   * Easy access to Funf config. As long as this service is running, changes will be automatically
   * picked up.
   */
  public static FunfConfig getMainConfig(Context context) {
    FunfConfig config = getConfig(context, MAIN_CONFIG);
    if (config.getName() == null) {
      String jsonString = getStringFromAsset(context, "default_config.json");
      if (jsonString == null) {
        Log.e(TAG, "Error loading default config.  Using blank config.");
        jsonString = "{}";
      }
      try {
        config.edit().setAll(jsonString).commit();
      } catch (JSONException e) {
        Log.e(TAG, "Error parsing default config", e);
      }
    }
    return config;
  }

  static boolean mReloaded = false;
  public static FunfConfig getMainConfigAlwaysReload(Context context) {
    FunfConfig config = getConfig(context, MAIN_CONFIG);
    if (!mReloaded) {
      String jsonString = getStringFromAsset(context, "default_config.json");
      if (jsonString == null) {
        Log.e(TAG, "Error loading default config.  Using blank config.");
        jsonString = "{}";
      }
      try {
        config.edit().setAll(jsonString).commit();
      } catch (JSONException e) {
        Log.e(TAG, "Error parsing default config", e);
      }
      mReloaded = true;
    }
    return config;
  }

  public static String getStringFromAsset(Context context, String filename) {
    InputStream is = null;
    try {
      is = context.getAssets().open(filename);
      return IOUtils.inputStreamToString(is, Charset.defaultCharset().name());
    } catch (IOException e) {
      Log.e(TAG, "Unable to read asset to string", e);
      return null;
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          Log.e(TAG, "Unable to close asset input stream", e);
        }
      }
    }
  }

  public void runProbeOnceNow(final String probeName) {
    FunfConfig config = getMainConfig(this);
    ArrayList<Bundle> updatedRequests = new ArrayList<Bundle>();
    Bundle[] existingRequests = config.getDataRequests(probeName);
    if (existingRequests != null) {
      for (Bundle existingRequest : existingRequests) {
        updatedRequests.add(existingRequest);
      }
    }

    Bundle oneTimeRequest = new Bundle();
    oneTimeRequest.putLong(Probe.Parameter.Builtin.PERIOD.name, 0L);
    updatedRequests.add(oneTimeRequest);

    Intent request = new Intent(Probe.ACTION_REQUEST);
    request.setClassName(this, probeName);
    request.putExtra(Probe.CALLBACK_KEY, getCallback());
    request.putExtra(Probe.REQUESTS_KEY, updatedRequests);
    startService(request);
  }

  public Class<?> getPactServiceClass() {
    return PactService.class;
  }

  protected void sendConfigToPactService(String json) {
    Intent intent = new Intent(this, getPactServiceClass());
    intent.setAction(PactService.ACTION_CONFIG_UPDATE);
    intent.putExtra(PactService.JSON_CONFIG, json);
    startService(intent);
  }

  protected void sendDataToPactService(Bundle data) {
    Intent intent = new Intent(this, getPactServiceClass());
    intent.setAction(PactService.ACTION_PROBE_DATA);
    intent.putExtras(data);
    startService(intent);
  }
}
