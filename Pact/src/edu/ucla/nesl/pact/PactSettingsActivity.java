package edu.ucla.nesl.pact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import edu.ucla.nesl.R;
import edu.ucla.nesl.funf.MainPipeline;
import edu.ucla.nesl.funf.NearbyPlacesProbe;

public class PactSettingsActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    registerButtonAction(R.id.btnReload, new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(PactSettingsActivity.this, MainPipeline.class);
        startService(intent);
      }
    });

    registerButtonAction(R.id.btnRunOnce, new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(PactSettingsActivity.this, MainPipeline.class);
        intent.setAction(MainPipeline.ACTION_RUN_ONCE);
        intent.putExtra(MainPipeline.RUN_ONCE_PROBE_NAME, NearbyPlacesProbe.class.getName());
        startService(intent);
      }
    });
  }

  private void registerButtonAction(int resId, OnClickListener listener) {
      Button b = (Button)findViewById(resId);
      b.setOnClickListener(listener);
  }
}
