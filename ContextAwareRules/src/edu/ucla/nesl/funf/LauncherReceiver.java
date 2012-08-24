package edu.ucla.nesl.funf;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LauncherReceiver extends BroadcastReceiver {

  private static boolean launched = false;

  public static void launch(Context context) {
    startService(context, MainPipeline.class); // Ensure main funf system is running
    launched = true;
  }

  public static void startService(Context context, Class<? extends Service> serviceClass) {
    Intent i = new Intent(context.getApplicationContext(), serviceClass);
    context.getApplicationContext().startService(i);
  }

  public static boolean isLaunched() {
    return launched;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    launch(context);
  }


}
