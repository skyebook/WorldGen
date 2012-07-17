package net.skyebook.worldgen.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
public class NodeHandler extends DefaultHandler {

    private XMLReader reader;
    private OSMHandler parent;
    private HashMap<Long, Node> nodeCache;
    private StringBuilder content;
    private Node node;
    private List<NodeWayRelationBaseObject> objects;

    public NodeHandler(XMLReader reader, OSMHandler parent, HashMap<Long, Node> nodeCache, HashMap<Long, Way> wayCache, HashMap<Long, Relation> relationCache, List<NodeWayRelationBaseObject> objects, Attributes attributes) {
        this.reader = reader;
        this.parent = parent;
        this.nodeCache = nodeCache;
        this.objects = objects;
        this.content = new StringBuilder();
        this.node = new Node();

        String id = attributes.getValue("id");
        node.setId(Long.parseLong(id));
        node.setUser(attributes.getValue("user"));
        node.setUid(Integer.parseInt(attributes.getValue("uid")));
        node.setChangeset(Integer.parseInt(attributes.getValue("changeset")));
        node.setVersion(Integer.parseInt(attributes.getValue("version")));
        node.setVisible(Boolean.parseBoolean(attributes.getValue("visible")));
        // TODO: Visible
        node.setTimestamp(parseDate(attributes.getValue("timestamp")));

        // The OSM API no longer sends position data for deleted nodes
        if (node.isVisible()) {
            node.setLatitude(Double.parseDouble(attributes.getValue("lat")));
            node.setLongitude(Double.parseDouble(attributes.getValue("lon")));
        }

        objects.add(node);
        nodeCache.put(node.getId(), node);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        content.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        content.setLength(0);
        //System.out.println("Node Start " + name);
        if (name.equals("tag")) {
            //System.out.println("Found Tag");
            reader.setContentHandler(new TagHandler(reader, this, node.getTags()));
        }
        if (name.equals("node")) {
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (name.equals("node")) {
            reader.setContentHandler(parent);
        }
    }

    /**
     * Taken from JOSM and adapted a bit
     *
     * @param value
     * @param parameter
     * @return
     */
    private Date parseDate(String value) {
        if (value == null || value.trim().equals("")) {
            return null;
        }
        if (value.endsWith("Z")) {
            // OSM API generates date strings we time zone abbreviation "Z" which Java SimpleDateFormat
            // doesn't understand. Convert into GMT time zone before parsing.
            //
            value = value.substring(0, value.length() - 1) + "GMT+00:00";
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }
}