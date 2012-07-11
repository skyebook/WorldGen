package net.skyebook.worldgen.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.skyebook.osmutils.NodeWayRelationBaseObject;
import org.xml.sax.SAXException;

/**
 *
 * @author skyebook
 */
public class OSMParserUtil {

    public static List<NodeWayRelationBaseObject> parseOSM(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        OSMHandler parser = new OSMHandler(saxParser.getXMLReader());
        saxParser.parse(is, parser);
        List<NodeWayRelationBaseObject> objects = parser.getObjects();
        return objects;
    }
}
