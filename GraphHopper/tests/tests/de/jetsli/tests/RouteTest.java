package de.jetsli.tests;

import android.os.Environment;
import android.test.AndroidTestCase;

import de.jetsli.graph.reader.OSMReader;
import de.jetsli.graph.routing.DijkstraBidirection;
import de.jetsli.graph.routing.Path;
import de.jetsli.graph.storage.Graph;
import de.jetsli.graph.storage.Location2IDFullIndex;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO: Give a one line description.
 *          gi
 * @author Kasturi Rangan Raghavan (kastur@gmail.com)
 */
public class RouteTest extends AndroidTestCase {

  public void testRoute() throws IOException, FileNotFoundException  {

    Graph graph = loadWestwood();

    Location2IDFullIndex locIndex = new Location2IDFullIndex(graph);

    int fromId = locIndex.findID(34.069144, -118.443199);  // Boelter Hall.
    int toId = locIndex.findID(34.063666, -118.451397);  // Weyburn Terrace
    DijkstraBidirection algo = new DijkstraBidirection(graph);
    Path path = algo.calcPath(fromId, toId);

  }

  private Graph loadWestwood() throws IOException, FileNotFoundException {
    final String storageDirectory = Environment.getDataDirectory().getPath() + "/graph_storage";
    new File(storageDirectory).mkdir();

    final int storageSize = 5000000;
    OSMReader reader = new OSMReader(storageDirectory, storageSize);

    String[] list = getContext().getAssets().list("ok");
    {
      InputStream inputStream = getContext().getAssets().open("westwood.osm");
      try {
        reader.preprocessAcceptHighwaysOnly(inputStream);
      } catch (XmlPullParserException ex) {
        System.err.println("Error!");
      }
    }
    {
      InputStream inputStream = getContext().getAssets().open("westwood.osm");
      reader.writeOsm2Graph(inputStream);
    }

    return reader.getGraph();
  }

}
