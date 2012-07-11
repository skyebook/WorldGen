package net.skyebook.worldgen.parser;

import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads an OSM tag
 * @author Skye Book
 */
public class TagHandler extends DefaultHandler {

    private XMLReader reader;
    private DefaultHandler parent;
    private HashMap<String, String> tags;

    public TagHandler(XMLReader reader, DefaultHandler parent, HashMap<String, String> tags) {
        this.reader = reader;
        this.parent = parent;
        this.tags = tags;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        String key = attributes.getValue("k");
        String value = attributes.getValue("v");
        tags.put(key, value);
    }
    
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (name.equals("tag")) {
            reader.setContentHandler(parent);
        }
    }
}