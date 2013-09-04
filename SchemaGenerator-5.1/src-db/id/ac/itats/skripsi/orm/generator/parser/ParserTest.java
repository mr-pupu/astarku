package id.ac.itats.skripsi.orm.generator.parser;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import id.ac.itats.skripsi.orm.generator.parser.model.OSM;
import id.ac.itats.skripsi.orm.generator.parser.model.OSMNode;
import id.ac.itats.skripsi.orm.generator.parser.model.Way;

public class ParserTest {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		OSM osm = OSMParser.parse("data/osm/surabaya_valid.osm");
		for (Way way : osm.getWays()){
			System.out.println(way.getName());
		}
	}
}
