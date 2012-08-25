package edu.ucla.nesl.funf;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import edu.mit.media.funf.probe.Probe;
import edu.ucla.nesl.pact.PactService;

public class LauncherReceiver extends BroadcastReceiver {

  private static final String prefix = "edu.ucla.nesl.funf.";
  public static final String ACTION_RESET = prefix + "ACTION_RESET";
  public static final String ACTION_PLACE_DEBUG = prefix + "ACTION_PLACE_DEBUG";

  public static void startService(Context context, Class<? extends Service> serviceClass) {
    Intent i = new Intent(context.getApplicationContext(), serviceClass);
    context.getApplicationContext().startService(i);
  }


  @Override
  public void onReceive(Context context, Intent intent) {
    final String action = intent.getAction();
    if (action != null) {
      if (action.equals(ACTION_RESET)) {
        startService(context, MainPipeline.class);
      } else if (action.equals(ACTION_PLACE_DEBUG)) {
        Bundle data = intent.getExtras();
        if (data == null) {
          return;
        }

        data.putString(Probe.PROBE, NearbyPlacesProbe.class.getName());
        data.putLong(Probe.TIMESTAMP, System.currentTimeMillis());

        Intent startIntent = new Intent(context, PactService.class);
        startIntent.setAction(PactService.ACTION_PROBE_DATA);
        startIntent.putExtras(data);
        context.getApplicationContext().startService(startIntent);
      }
    }
  }
}
