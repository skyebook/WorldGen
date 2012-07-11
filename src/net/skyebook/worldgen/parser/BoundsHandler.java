package net.skyebook.worldgen.parser;

import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads an OSM document's bounds element
 *
 * @author Skye Book
 */
public class BoundsHandler extends DefaultHandler {

    private XMLReader reader;
    private DefaultHandler parent;
    private HashMap<String, String> tags;

    public BoundsHandler(XMLReader reader, DefaultHandler parent, Attributes attributes) {
        this.reader = reader;
        this.parent = parent;
        
        double minLat = Double.parseDouble(attributes.getValue("minlat"));
        double minLon = Double.parseDouble(attributes.getValue("minlon"));
        double maxLat = Double.parseDouble(attributes.getValue("maxlat"));
        double maxLon = Double.parseDouble(attributes.getValue("maxlon"));

        System.out.println("Document bounds: [" + minLat + "," + maxLat + "][" + minLon + "," + maxLon + "]");
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (name.equals("bounds")) {
            reader.setContentHandler(parent);
        }
    }
}