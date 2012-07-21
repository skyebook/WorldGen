package net.skyebook.worldgen.test;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import net.skyebook.osmutils.Node;
import net.skyebook.osmutils.NodeWayRelationBaseObject;
import net.skyebook.osmutils.Way;
import net.skyebook.tmsvec3f.CamRegistrationTMSAppState;
import net.skyebook.tmsvec3f.LatLon;
import net.skyebook.worldgen.GeometryGenerator;
import net.skyebook.worldgen.parser.OSMParserUtil;
import org.xml.sax.SAXException;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private CamRegistrationTMSAppState tms;
    private GeometryGenerator generator;
    private int count = 0;

    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.SEVERE);
        
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // This keeps the camera synchronized with a coordinate system
        tms = new CamRegistrationTMSAppState(new LatLon(40.702, -74.015), 15, 10);

        // Create OpenStreetMap Geometry
        generator = new GeometryGenerator(assetManager);
        try {
            List<NodeWayRelationBaseObject> objects = OSMParserUtil.parseOSM(new FileInputStream(new File("test_data/nyc.osm.xml")));
            for (NodeWayRelationBaseObject object : objects) {
                Geometry geometry = generator.renderGeometry(object);
                if (geometry != null) {
                    //System.out.println("Adding " + geometry.getName());

                    LatLon mountCoordinate = null;
                    if (object instanceof Node) {
                        mountCoordinate = new LatLon(((Node) object).getLatitude(), ((Node) object).getLongitude());
                    }
                    else if (object instanceof Way) {
                        // Use the first node in this way
                        Node firstNode = ((Way) object).getMembers().get(0);
                        mountCoordinate = new LatLon(firstNode.getLatitude(), firstNode.getLongitude());
                    }

                    if (mountCoordinate != null) {
                        Vector3f mountPoint = tms.getCoordinateSystem().getLatLonPlacement(mountCoordinate);
                        //System.out.println("Mounting At " + mountPoint);
                        geometry.setLocalTranslation(mountPoint);
                        rootNode.attachChild(geometry);
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


        flyCam.setMoveSpeed(512);
        cam.setFrustumFar(10000);

        stateManager.attach(tms);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (count == 60) {
            LatLon coordinate = tms.getCoordinateSystem().getLocationCoordinate(cam.getLocation());
            System.out.println("Camera at " + coordinate);
            count = 0;
        }
        else {
            count++;
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
