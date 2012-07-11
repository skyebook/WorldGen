package net.skyebook.worldgen.test;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import net.skyebook.tmsvec3f.CamRegistrationTMSAppState;
import net.skyebook.tmsvec3f.LatLon;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    private CamRegistrationTMSAppState tms;
    
    private int count = 0;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        flyCam.setMoveSpeed(512);
	cam.setFrustumFar(10000);
        
        // This keeps the camera synchronized with a coordinate system
	tms = new CamRegistrationTMSAppState(new LatLon(40, -75), 15, 10);
	stateManager.attach(tms);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if(count==60){
            LatLon coordinate = tms.getCoordinateSystem().getLocationCoordinate(cam.getLocation());
            System.out.println("Camera at " + coordinate);
            count=0;
        }
        else{
            count++;
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
