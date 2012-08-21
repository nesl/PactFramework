package edu.ucla.nesl.pact;

import static edu.mit.media.funf.AsyncSharedPrefs.async;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import edu.mit.media.funf.IOUtils;
import edu.mit.media.funf.Utils;
import edu.mit.media.funf.configured.ConfiguredPipeline;
import edu.mit.media.funf.configured.FunfConfig;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.storage.BundleSerializer;


public class MainPipeline extends ConfiguredPipeline {
	
	public static final String TAG = "FunfBGCollector";
	public static final String MAIN_CONFIG = "main_config";
	public static final String START_DATE_KEY = "START_DATE";

	public static final String ACTION_RUN_ONCE = "RUN_ONCE";
	public static final String RUN_ONCE_PROBE_NAME = "PROBE_NAME";
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if (ACTION_RUN_ONCE.equals(intent.getAction())) {
			String probeName = intent.getStringExtra(RUN_ONCE_PROBE_NAME);
			runProbeOnceNow(probeName);
        } else if (intent.getStringExtra("EVENT_NAME") != null) {
            handleInstrumentationIntent(intent);
		} else {
			super.onHandleIntent(intent);
		}
	}

    private void handleInstrumentationIntent(Intent intent) {
        final String event = intent.getStringExtra("EVENT_NAME");
        final String pkg = intent.getStringExtra("PKG_NAME");
        final String cls = intent.getStringExtra("CLS_NAME");
        Bundle data = new Bundle();
        data.putString(Probe.PROBE, "Instrumentation");
        data.putLong(Probe.TIMESTAMP, System.currentTimeMillis());
        data.putString("event", event);
        data.putString("pkg", pkg);
        data.putString("cls", cls);
        onDataReceived(data);
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
        sendDataToPrivacyService(data);
	}

    private void sendDataToPrivacyService(Bundle data) {
        Intent i = new Intent(this, getPrivacyServiceClass());
        i.setAction(PrivacyService.ACTION_REPORT_DATA);
        i.putExtras(data);
        startService(i);
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
		return async(context.getSharedPreferences(MainPipeline.class.getName() + "_system", MODE_PRIVATE));
	}
	
	@Override
	public FunfConfig getConfig() {
		return getMainConfig(this);
	}

	/**
	 * Easy access to Funf config.  
	 * As long as this service is running, changes will be automatically picked up.
	 * @param context
	 * @return
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

    public Class<?> getPrivacyServiceClass() {
        return PrivacyService.class;
    }

    @Override
    public void updateConfig(String jsonString) {

        RulesParser parser = new RulesParser();
        parser.loadConfigFromJson(jsonString);
        final String funfString = parser.getFunfConfigString();
        super.updateConfig(funfString);
    }
}
