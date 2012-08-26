package edu.ucla.nesl.funf;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.util.Log;

import com.bbn.openmap.util.quadtree.QuadTree;

import java.io.Reader;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Vector;

public class PlacesManager {

  private static final float  METERS_PER_LAT_DEGREE_FOR_LOS_ANGELES = 110922.36461376304f;


  private static final String TAG = "PlacesManager";
  private static final int NUM_PLACE_TYPES = 3;
  private QuadTree mIndex;
  private boolean mLoaded = false;

  public void LoadFromJson(Reader reader) {
    mIndex = new QuadTree();
    final Reader final_reader = reader;
    Thread loader = new Thread() {
      @Override
      public void run() {
        Log.d(TAG, "----------STARTED TO LOAD POIs");
        JsonElement root = new JsonParser().parse(final_reader);
        int count = 0;
        for (Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet()) {
          if (count > NUM_PLACE_TYPES)
            break;
          final String key = entry.getKey();
          for (JsonElement o : entry.getValue().getAsJsonArray()) {
            JsonArray list = o.getAsJsonArray();
            final float lon = list.get(0).getAsFloat();
            final float lat = list.get(1).getAsFloat();
            // If we want to, we can also put the name into the quad-tree.
            // String name = list.get(2).getAsString();
            mIndex.put(lat, lon, key);
          }
          Log.d(TAG, "Loaded: #" + count + " : " + key);
          count++;
        }
        mLoaded = true;
        Log.d(TAG, "----------FINISHED LOADING POIs");
      }
    };
    loader.start();
  }

  public void clear() {
    mIndex.clear();
  }

  public HashSet<String> getNearbyPlaces(float lat, float lon, float accuracy_meters) {
    if (!mLoaded) {
      return new HashSet<String>();
    }

    // TODO: Remove this hack to convert meters to latitude/longitude degrees.
    float eps = (accuracy_meters + 100.0f) / METERS_PER_LAT_DEGREE_FOR_LOS_ANGELES;

    Vector<String> vector = mIndex.get(lat + eps, lon - eps, lat - eps, lon + eps);
    HashSet<String> hashSet = new HashSet<String>(vector);
    //HashSet<String> hashSet = new HashSet<String>();
    //hashSet.add(s);
    return hashSet;
  }
}
