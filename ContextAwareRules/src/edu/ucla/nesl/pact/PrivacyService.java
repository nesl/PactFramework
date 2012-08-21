package edu.ucla.nesl.pact;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PrivacyService extends Service {
    private static final String TAG = "PrivacyService";

    public static final String ACTION_REPORT_DATA = "ACTION_REPORT_DATA";

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Log.d(TAG, intent.getExtras().get("PROBE").toString());
        return super.onStartCommand(intent, flags, startId);
    }
}
