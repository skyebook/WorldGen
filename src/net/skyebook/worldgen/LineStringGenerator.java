package net.skyebook.worldgen;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import net.skyebook.osmutils.Node;
import net.skyebook.tmsvec3f.CoordinateSystem;
import net.skyebook.tmsvec3f.LatLon;
import net.skyebook.tmsvec3f.Tile;

/**
 *
 * @author Skye Book
 */
public class LineStringGenerator implements MeshGenerator{

    private List<Node> nodeLine;
    private float width;
    
    public LineStringGenerator(List<Node> nodeLine, float width){
        this.nodeLine = nodeLine;
        this.width = width;
    }

    public Mesh generateMesh() {
        
        // Create a localized coordinate system and calculate the meshe's offset from the origin
        Node firstNode = nodeLine.get(0);
        // TODO: Get extents of node line and use an appropriate tile zoom level
        CoordinateSystem cs = new CoordinateSystem(Tile.getTileNumber(new LatLon(firstNode.getLatitude(), firstNode.getLongitude()), 15));
        Vector3f offset = cs.getLatLonPlacement(new LatLon(firstNode.getLatitude(), firstNode.getLongitude()));
        
        System.out.println("OFFSET: " + offset);
        
        List<Vector3f> centerLine = new ArrayList<Vector3f>();
        
        for(Node node : nodeLine){
            Vector3f point = cs.getLatLonPlacement(new LatLon(node.getLatitude(), node.getLongitude()));
            point.subtractLocal(offset);
            centerLine.add(point);
        }
        
        
        
        // storage vectors
        Vector3f tempDir = new Vector3f();
        Vector3f tempLoc = new Vector3f();

        ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

        for (int i = 0; i < centerLine.size(); i++) {
            Vector3f thisPoint = centerLine.get(i);

            // if this is the first point, generate the starting left/right points
            if (i == 0) {
                Vector3f nextPoint = centerLine.get(i + 1);
                // store the direction of this segment into tempDir
                nextPoint.subtract(thisPoint, tempDir);
                tempDir.normalizeLocal();
                tempDir.crossLocal(Vector3f.UNIT_Y);

                // create the left vertex
                thisPoint.clone().add(tempDir.mult(-1 * (width / 2)), tempLoc);
                vertices.add(tempLoc.clone());
                // create the right vertex
                thisPoint.clone().add(tempDir.mult(width / 2), tempLoc);
                vertices.add(tempLoc.clone());
            }

            // we need at least two more vertices to correctly calculate the angles
            if (i < centerLine.size() - 2) {
                Vector3f secondPoint = centerLine.get(i + 1);
                Vector3f thirdPoint = centerLine.get(i + 2);

                // get the angle between this point and the third point
                thirdPoint.subtract(thisPoint, tempDir);
                tempDir.normalizeLocal();
                tempDir.crossLocal(Vector3f.UNIT_Y);

                /* create an angle to the left of the second point
                 * using the direction between the first and third angles */
                // create the left vertex
                secondPoint.clone().add(tempDir.mult(-1 * (width / 2)), tempLoc);
                vertices.add(tempLoc.clone());
                // create the right vertex
                secondPoint.clone().add(tempDir.mult(width / 2), tempLoc);
                vertices.add(tempLoc.clone());
            }
            else if (i == centerLine.size() - 2) {
                // this is the second to last item
                Vector3f nextPoint = centerLine.get(i + 1);
                // store the direction of this segment into tempDir
                nextPoint.subtract(thisPoint, tempDir);
                tempDir.normalizeLocal();
                tempDir.crossLocal(Vector3f.UNIT_Y);

                // create the left vertex
                thisPoint.clone().add(tempDir.mult(-1 * (width / 2)), tempLoc);
                vertices.add(tempLoc.clone());
                // create the right vertex
                thisPoint.clone().add(tempDir.mult(width / 2), tempLoc);
                vertices.add(tempLoc.clone());
            }
            else {
                // this is the last point, use the previously set direction to find left&right points

                thisPoint.clone().add(tempDir.mult(-1 * (width / 2)), tempLoc);
                vertices.add(tempLoc.clone());
                // create the right vertex
                thisPoint.clone().add(tempDir.mult(width / 2), tempLoc);
                vertices.add(tempLoc.clone());
            }
        }

        FloatBuffer vertexBuffer = BufferUtils.createVector3Buffer(vertices.size());
        vertexBuffer.rewind();
        for (Vector3f v : vertices) {
            vertexBuffer.put(v.x);
            vertexBuffer.put(v.y);
            vertexBuffer.put(v.z);
        }

        // create the index buffer (form triangles)
        // NOTE:  Triangles are created in the GL_TRIANGLE_STRIP format
        IntBuffer faces = BufferUtils.createIntBuffer(vertices.size());
        faces.rewind();

        // the vertices were created in the correct order, so we just need to put numbers in
        for (int i = 0; i < vertices.size(); i++) {
            faces.put(i);
        }

        //FloatBuffer coords;
        //TexCoords tc = new TexCoords(null, 2);

        //FloatBuffer normals = FloatBuffer.allocate(faces.capacity()/3);
        FloatBuffer normals = BufferUtils.createVector3Buffer(vertices.size());
        normals.rewind();
        for (int i = 0; i < vertices.size(); i++) {
            normals.put(0);
            normals.put(1);
            normals.put(0);
        }

        Mesh mesh = new Mesh();
        mesh.setMode(Mode.TriangleStrip);
        mesh.setBuffer(Type.Position, 3, vertexBuffer);
        mesh.setBuffer(Type.Normal, 3, normals);
        mesh.setBuffer(Type.Index, 3, faces);
        mesh.updateBound();
        return mesh;
    }
}
