package id.ac.itats.skripsi.orm.generator.parser;


import id.ac.itats.skripsi.orm.generator.parser.model.OSM;
import id.ac.itats.skripsi.orm.generator.parser.model.OSMNode;
import id.ac.itats.skripsi.orm.generator.parser.model.Relation;
import id.ac.itats.skripsi.orm.generator.parser.model.Way;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Willy Tiengo
 */
public class OSMParser {

    /**
     * @param args the command line arguments
     */
	// throws-declaration changed by Joris Maervoet, KaHoSL
    public static OSM parse(String path) throws ParserConfigurationException, SAXException, IOException  {

        Document doc;
        DocumentBuilder builder;

        Node node;
        NodeList nodesList;

        Map<String, OSMNode> nodes = new LinkedHashMap<String, OSMNode>();

        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        doc = builder.parse(path);

        nodesList = doc.getChildNodes().item(0).getChildNodes();

        OSM osm = new OSM();
     
        for (int i = 0; i < nodesList.getLength(); i++) {

            node = nodesList.item(i);
         
            if (NodeParser.isNode(node)) {
            	
                OSMNode osmNode = NodeParser.parseNode(node);
                nodes.put(osmNode.id, osmNode);
                osm.getNodes().add(osmNode);
 
            } else if (WayParser.isWay(node)) {

                Way way = WayParser.parseWay(node, nodes);
                // Changed by Joris Maervoet, KaHoSL
                if (way != null) osm.getWays().add(way);

            } else if (RelationParser.isRelation(node)) {

                Relation relation = RelationParser.parseRelation(osm, node);
                osm.getRelations().add(relation);

            }
        }

        Set<OSMNode> nodeset = new HashSet<OSMNode>();

        for (String n : nodes.keySet()) {
            nodeset.add(nodes.get(n));
        }

        return osm;
    }

    protected static Map<String, String> parseTags(NodeList nodes) {

        Map<String, String> tags = new HashMap<String, String>();

        for (int i = 0; i < nodes.getLength(); i++) {

            Node node = nodes.item(i);

            if (node.getNodeName().equals("tag")) {

                addTag(tags, node);

            }
        }

        return tags;
    }

    private static void addTag(Map<String, String> tags, Node node) {
        String key = node.getAttributes().getNamedItem("k").getNodeValue();
        String value = node.getAttributes().getNamedItem("v").getNodeValue();

        if (tags.get(key) != null) {

            tags.put(key, tags.get(key) + ";" + value);

        } else {

            tags.put(key, value);

        }
    }
}
