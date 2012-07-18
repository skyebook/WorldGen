package net.skyebook.worldgen;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.skyebook.osmutils.Node;
import net.skyebook.osmutils.NodeWayRelationBaseObject;
import net.skyebook.osmutils.Relation;
import net.skyebook.osmutils.TaggableObject;
import net.skyebook.osmutils.Way;
import net.skyebook.tmsvec3f.CoordinateSystem;
import net.skyebook.tmsvec3f.LatLon;
import net.skyebook.tmsvec3f.Tile;

/**
 *
 * @author Skye Book
 */
public class GeometryGenerator {

    private AssetManager assetManager;
    private Map<String, Map<String, Geometry>> geometryCache;

    public GeometryGenerator(AssetManager assetManager) {
        this.assetManager = assetManager;

        geometryCache = new HashMap<String, Map<String, Geometry>>();
    }

    /**
     * Render an OpenStreetMap object to a geometry
     *
     * @param object
     * @return
     */
    public Geometry renderGeometry(NodeWayRelationBaseObject object) {
        if (object instanceof Node) {
            return renderNode((Node) object);
        } else if (object instanceof Way) {
            return renderWay((Way) object);
        } else if (object instanceof Relation) {
            return renderRelation((Relation) object);
        } else {
            return null;
        }
    }

    private Geometry renderNode(Node node) {
        if (checkForTag(node, "emergency")) {
            String emergencyValue = getValueForTag(node, "emergency");
            Map<String, Geometry> cache = getTagCache("emergency");

            /* If the geometry object is already in the cache, perform a shallow
             * clone, replace the user data with this node's object, and return
             * the geometry
             */
            Geometry geometry = cache.get(emergencyValue);
            if (geometry != null) {
                geometry = geometry.clone(false);
                geometry.setUserData("osm_object", node);
                return geometry;
            } else {
                if (emergencyValue.equals("fire_hydrant")) {
                    // Fire hydrants are 66cm tall.. not sure about their other dimensions
                    Box box = new Box(.25f, .66f, .25f);
                    geometry = new Geometry("fire_hydrant", box);
                    geometry.setUserData("osm_object", node);
                    geometry.setMaterial(Util.coloredUnshaded(assetManager, ColorRGBA.Red));
                    return geometry;
                }
            }
        }
        return null;
    }

    /**
     * Get the cache for a tag name. If the cache doesn't exist it is created.
     * This should be safe for multithreaded access.
     *
     * @param tagName
     * @return
     */
    private synchronized Map<String, Geometry> getTagCache(String tagName) {
        Map<String, Geometry> cache = geometryCache.get(tagName);
        if (cache == null) {
            cache = new HashMap<String, Geometry>();
            geometryCache.put(tagName, cache);
        }

        return cache;
    }

    private Geometry renderWay(Way way) {
        if (checkForTag(way, "highway")) {
            String highwayValue = getValueForTag(way, "highway");
            
            // TODO: Set width depending on tags
            float width = 10;
            
            Node firstNode = way.getMembers().get(0);
            // Create a coordinate system with its origin near this way
            CoordinateSystem cs = new CoordinateSystem(Tile.getTileNumber(new LatLon(firstNode.getLatitude(), firstNode.getLongitude()), 15));
            ArrayList<Vector3f> centerLine = new ArrayList<Vector3f>();
            for(Node node : way.getMembers()){
                centerLine.add(cs.getLatLonPlacement(new LatLon(node.getLatitude(), node.getLongitude())));
            }
            LineStringGenerator lsg = new LineStringGenerator(centerLine, width);
            Mesh mesh = lsg.generateMesh();
            Geometry geometry = new Geometry("way_"+way.getId(), mesh);
            geometry.setUserData("osm_object", way);
            geometry.setMaterial(Util.coloredUnshaded(assetManager, ColorRGBA.Gray));
            return geometry;
        }
        return null;
    }

    private Geometry renderRelation(Relation relation) {
        return null;
    }

    private boolean checkForTag(TaggableObject object, String tagName) {
        return getValueForTag(object, tagName) != null;
    }

    private String getValueForTag(TaggableObject object, String tagName) {
        return object.getTags().get(tagName);
    }
}
