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
 * @author skyebook
 */
public class WayHandler extends DefaultHandler {

    private XMLReader reader;
    private OSMHandler parent;
    private HashMap<Long, Node> nodeCache;
    private StringBuilder content;
    private Way way;
    private List<NodeWayRelationBaseObject> objects;

    public WayHandler(XMLReader reader, OSMHandler parent, HashMap<Long, Node> nodeCache, HashMap<Long, Way> wayCache, HashMap<Long, Relation> relationCache, List<NodeWayRelationBaseObject> objects, Attributes attributes) {
        this.reader = reader;
        this.parent = parent;
        this.nodeCache = nodeCache;
        this.objects = objects;
        this.content = new StringBuilder();
        this.way = new Way();
        String id = attributes.getValue("id");
        way.setId(Long.parseLong(id));
        way.setUser(attributes.getValue("user"));
        way.setUid(Integer.parseInt(attributes.getValue("uid")));
        way.setChangeset(Integer.parseInt(attributes.getValue("changeset")));
        way.setVersion(Integer.parseInt(attributes.getValue("version")));
        // TODO: Visible
        way.setTimestamp(parseDate(attributes.getValue("timestamp")));
        
        objects.add(way);
        wayCache.put(way.getId(), way);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        content.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        content.setLength(0);

        if (name.equals("tag")) {
            reader.setContentHandler(new TagHandler(reader, this, way.getTags()));
        }
        if (name.equals("nd")) {
            //System.out.println("Found Way Member");
            long nodeID = Long.parseLong(attributes.getValue("ref"));
            Node node = nodeCache.get(nodeID);
            if (node != null) {
                way.getMembers().add(node);
            }
            else {
                //System.out.println("ERROR: Node " + nodeID + " not found in cache");
            }
        }
        if (name.equals("way")) {
        }

    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (name.equals("way")) {
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