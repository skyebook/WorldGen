package net.skyebook.worldgen.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import net.skyebook.osmutils.Node;
import net.skyebook.osmutils.NodeWayRelationBaseObject;
import net.skyebook.worldgen.parser.OSMParserUtil;
import org.xml.sax.SAXException;

/**
 *
 * @author skyebook
 */
public class XMLParserTest {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        List<NodeWayRelationBaseObject> objects = OSMParserUtil.parseOSM(new FileInputStream(new File("test_data/nyc.osm.xml")));
        for(NodeWayRelationBaseObject object : objects){
            if(object instanceof Node){
                System.out.println("Node has " + object.getTags().size());
            }
        }
        System.out.println("Parsed " + objects.size() + " objects");
    }
}
