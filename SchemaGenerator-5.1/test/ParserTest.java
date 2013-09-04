import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import id.ac.itats.skripsi.orm.generator.parser.OSMParser;
import id.ac.itats.skripsi.orm.generator.parser.model.OSM;
import id.ac.itats.skripsi.orm.generator.parser.model.OSMNode;
import id.ac.itats.skripsi.orm.generator.parser.model.Way;

public class ParserTest {
	OSM osm;

	public ParserTest() {
		try {
			osm = OSMParser.parse("data/osm/surabaya_valid.osm");

			System.out.println("parse finish!");

		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Set<Way> getWays() {
		return osm.getWays();
	}

	public Set<OSMNode> getOSMNode() {
		return osm.getNodes();
	}

	// XXX LOCATION
	public void osmLocation() {
		for (OSMNode osmNode : getOSMNode()) {
			Map<String, String> tags = osmNode.tags;
			
			String id;
			String latLon;
			String amenity;
			String name;
			String address;

			if (tags.size() > 0) {
				
				if (tags.get("name") != null) {
					id=osmNode.id;
					latLon=osmNode.lat+","+osmNode.lon;
					amenity=tags.get("amenity") != null ? tags.get("amenity") : "village";
					name=tags.get("name");
					
					if (tags.containsKey("amenity")) {
						address = tags.get("addr:street");					
					} else {
						address = tags.get("is_in:county") + ", " + tags.get("is_in:region");						
					}
					
					address = address != null ? address : "N/A";
					
					if(address.equals("null, null")){
						address = "N/A";
					}
					
					if(name.equalsIgnoreCase("Surabaya")){
						amenity="city";
					}
					
					System.out.println(id +" - "+ latLon+" - " + amenity+" - " + name+" - " + address);
					
				}
			}

		}
	}

	public void osmStreet() {
		// XXX STREET
		for (Way osmWay : getWays()) {
			Map<String, String> tags = osmWay.tags;
			String id;
			String name; 			

			if (tags.size() > 0) {
				id = osmWay.id;
				name = tags.get("name") != null ? tags.get("name") : "N/A";
				
				System.out.println(id +" - "+name);
			}

		}
	}
	
	public void streetLocation(){
		for(Way way : getWays()){
			for(OSMNode node : way.getNodes()){
				System.out.println(way.id + " - "+node.id);
			}
		}
	}

	public static void main(String[] args) {
		ParserTest parser = new ParserTest();
//		for(Way way : parser.getWays()){
//			for(OSMNode node : way.getNodes()){
//				System.out.println(way.id + " - "+node.id);
//			}
//		}
		
//		for (OSMNode node : parser.getOSMNode()){
//			Map<String, String> tags = node.tags;
//			
//			if(tags.size()>0){
//				if(tags.containsKey("amenity")){
//					System.out.println(tags);
//				}
//				
//			}
//		}
		
		parser.osmLocation();
		
	}
}
