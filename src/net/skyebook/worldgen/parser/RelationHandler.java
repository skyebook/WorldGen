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
public class RelationHandler extends DefaultHandler {

    private XMLReader reader;
    private HashMap<Long, Node> nodeCache;
    private HashMap<Long, Way> wayCache;
    private HashMap<Long, Relation> relationCache;
    private StringBuilder content;
    private Relation relation;
    private List<NodeWayRelationBaseObject> objects;

    public RelationHandler(XMLReader reader, HashMap<Long, Node> nodeCache, HashMap<Long, Way> wayCache, HashMap<Long, Relation> relationCache, List<NodeWayRelationBaseObject> objects) {
        this.reader = reader;
        this.nodeCache = nodeCache;
        this.wayCache = wayCache;
        this.relationCache = relationCache;
        this.objects = objects;
        this.content = new StringBuilder();
        this.relation = new Relation();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        content.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        content.setLength(0);

        if (name.equals("tag")) {
            reader.setContentHandler(new TagHandler(reader, relation.getTags()));
        }
        if (name.equals("member")) {
            long memberID = Long.parseLong(attributes.getValue("id"));
            String type = attributes.getValue("type");
            String role = attributes.getValue("role");
            if (type.equals("node")) {
                Node node = nodeCache.get(memberID);
                if (node != null) {
                    relation.getMembers().add(node);
                }
                else {
                    System.out.println("ERROR: Node " + memberID + " not found in cache");
                }
            }
            else if (type.equals("way")) {
                Way way = wayCache.get(memberID);
                if (way != null) {
                    relation.getMembers().add(way);
                }
                else {
                    System.out.println("ERROR: Way " + memberID + " not found in cache");
                }
            }
            else if (type.equals("relation")) {
                Relation rel = relationCache.get(memberID);
                if (rel != null) {
                    relation.getMembers().add(rel);
                }
                else {
                    System.out.println("ERROR: Relation " + memberID + " not found in cache");
                }
            }
        }
        if (name.equals("relation")) {
            String id = attributes.getValue("id");
            relation.setId(Long.parseLong(id));
            relation.setUser(attributes.getValue("user"));
            relation.setUid(Integer.parseInt(attributes.getValue("uid")));
            relation.setChangeset(Integer.parseInt(attributes.getValue("changeset")));
            relation.setVersion(Integer.parseInt(attributes.getValue("version")));
            // TODO: Visible
            relation.setTimestamp(parseDate(attributes.getValue("timestamp")));

            objects.add(relation);
        }

    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
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