package net.skyebook.worldgen.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import net.skyebook.osmutils.Node;
import net.skyebook.osmutils.NodeWayRelationBaseObject;
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
    private XMLReader reader;
    private List<NodeWayRelationBaseObject> objects;

    public OSMParser(XMLReader reader) {
        this.reader = reader;
        this.nodeCache = new HashMap<Long, Node>();
        this.objects = new LinkedList<NodeWayRelationBaseObject>();
    }

    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (name.equals("node")) {
            // Switch handler to parse the team element
            reader.setContentHandler(new NodeHandler(reader, nodeCache));
        }
    }
}