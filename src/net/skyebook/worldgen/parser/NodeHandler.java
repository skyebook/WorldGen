/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.skyebook.worldgen.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import net.skyebook.osmutils.Node;
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
    private HashMap<Long, Node> nodeCache;
    private StringBuilder content;
    private Node node;

    public NodeHandler(XMLReader reader, HashMap<Long, Node> nodeCache) {
        this.reader = reader;
        this.nodeCache = nodeCache;
        this.content = new StringBuilder();
        this.node = new Node();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        content.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        content.setLength(0);
        String id = attributes.getValue("id");
        node.setId(Long.parseLong(id));
        node.setUser(attributes.getValue("user"));
        node.setUid(Integer.parseInt(attributes.getValue("uid")));
        node.setChangeset(Integer.parseInt(attributes.getValue("changeset")));
        node.setVersion(Integer.parseInt(attributes.getValue("version")));
        // TODO: Visible
        node.setTimestamp(parseDate(attributes.getValue("timestamp")));

        node.setLatitude(Double.parseDouble(attributes.getValue("lat")));
        node.setLongitude(Double.parseDouble(attributes.getValue("lon")));
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (name.equals("tag")) {
            HashMap<String, String> tags = new HashMap<String, String>();
            node.setTags(tags);
            reader.setContentHandler(new TagHandler(reader, tags));
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