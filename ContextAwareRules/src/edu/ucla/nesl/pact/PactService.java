package edu.ucla.nesl.pact;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * TODO: Give a one line description.
 *
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public class PactService extends Service {

  private static final String TAG = "PactService";

  public static final String ACTION_REPORT_DATA = "ACTION_REPORT_DATA";

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    return super.onStartCommand(intent, flags, startId);
  }
}
