package net.skyebook.worldgen.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.skyebook.osmutils.NodeWayRelationBaseObject;
import net.skyebook.worldgen.parser.OSMParser;
import org.xml.sax.SAXException;

/**
 *
 * @author skyebook
 */
public class XMLParserTest {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        OSMParser parser = new OSMParser(saxParser.getXMLReader());
        saxParser.parse(new File("test_data/map.osm.xml"), parser);
        List<NodeWayRelationBaseObject> objects = parser.getObjects();
        System.out.println("Parsed " + objects.size() + " objects");
    }
}
