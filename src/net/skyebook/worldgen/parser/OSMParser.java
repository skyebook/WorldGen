package net.skyebook.worldgen.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import net.skyebook.osmutils.Node;
import net.skyebook.osmutils.NodeWayRelationBaseObject;
import net.skyebook.osmutils.Relation;
import net.skyebook.osmutils.Way;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Skye Book
 */
public class OSMParser extends DefaultHandler {

    private HashMap<Long, Node> nodeCache;
    private HashMap<Long, Way> wayCache;
    private HashMap<Long, Relation> relationCache;
    private XMLReader reader;
    private List<NodeWayRelationBaseObject> objects;

    public OSMParser(XMLReader reader) {
        this.reader = reader;
        this.nodeCache = new HashMap<Long, Node>();
        this.wayCache = new HashMap<Long, Way>();
        this.relationCache = new HashMap<Long, Relation>();
        this.objects = new LinkedList<NodeWayRelationBaseObject>();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (name.equals("node")) {
            //System.out.println("Found Node");
            reader.setContentHandler(new NodeHandler(reader, this, nodeCache, wayCache, relationCache, objects, attributes));
        }
        else if (name.equals("way")) {
            //System.out.println("Found Way");
            reader.setContentHandler(new WayHandler(reader, this, nodeCache, wayCache, relationCache, objects, attributes));
        }
        else if (name.equals("relation")) {
            //System.out.println("Found Relation");
            reader.setContentHandler(new RelationHandler(reader, this, nodeCache, wayCache, relationCache, objects, attributes));
        }
    }
    
    public List<NodeWayRelationBaseObject> getObjects(){
        return objects;
    }
}