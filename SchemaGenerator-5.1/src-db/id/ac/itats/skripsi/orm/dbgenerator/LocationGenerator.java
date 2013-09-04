package id.ac.itats.skripsi.orm.dbgenerator;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import id.ac.itats.skripsi.orm.generator.parser.OSMParser;
import id.ac.itats.skripsi.orm.generator.parser.model.OSM;
import id.ac.itats.skripsi.orm.generator.parser.model.OSMNode;
import id.ac.itats.skripsi.orm.generator.parser.model.Way;

public class LocationGenerator {
	OSM osm;

	public LocationGenerator() {
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
	public void addPlace(Statement stmt) throws SQLException {
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
					name=tags.get("name").replaceAll("'", "");
					
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
										
					/*
						"CREATE TABLE " + constraint + "'PLACE' (" + //
		                "'PLACE_ID' INTEGER PRIMARY KEY ," + // 0: placeID
		                "'LAT_LON' TEXT," + // 1: latLon
		                "'CATEGORY' TEXT," + // 2: category
		                "'NAME' TEXT," + // 3: name
		                "'ADDRESS' TEXT);"); // 4: address			 
					 
					 */
					
//					System.out.println(id +" - "+ latLon+" - " + amenity+" - " + name+" - " + address);
					
					String insertPlace = "INSERT INTO 'PLACE' ('PLACE_ID', 'LAT_LON', 'CATEGORY', 'NAME', 'ADDRESS' )"
							+ " VALUES ('"
							+ id
							+ "','"
							+ latLon
							+ "','"
							+ amenity
							+ "','"
							+ name
							+ "','" 
							+ address
							+ "' );";
					stmt.executeUpdate(insertPlace);
					
				}
			}

		}
		stmt.close();
	}

	public void addRoad(Statement stmt) throws SQLException {
		// XXX STREET
		for (Way osmWay : getWays()) {
			Map<String, String> tags = osmWay.tags;
			String id;
			String name; 			

			if (tags.size() > 0) {
				id = osmWay.id;
				name = tags.get("name") != null ? tags.get("name") : "N/A";
								
				/*
					"CREATE TABLE " + constraint + "'ROAD' (" + //
	                "'ROAD_ID' INTEGER PRIMARY KEY ," + // 0: roadID
	                "'NAME' TEXT);"); // 1: name
				 */
				
				
//				System.out.println(id +" - "+name);
				
				String insertRoad = "INSERT INTO 'ROAD' ('ROAD_ID', 'NAME')"
						+ " VALUES ('"
						+ id
						+ "','"						
						+ name
						+ "' );";
				stmt.executeUpdate(insertRoad);
			}

		}
		stmt.close();
	}
	
	public void addRoadPlace(Statement stmt) throws SQLException {
		for(Way way : getWays()){
			for(OSMNode node : way.getNodes()){
				
				/*
					"CREATE TABLE " + constraint + "'ROAD_PLACE' (" + //
	                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
	                "'FK_ROAD' INTEGER," + // 1: fk_road
	                "'FK_PLACE' INTEGER);"); // 2: fk_place
				 
				 */
				
//				System.out.println(way.id + " - "+node.id);
				String insertRoadPlace = "INSERT INTO 'ROAD_PLACE' ('FK_ROAD', 'FK_PLACE')"
						+ " VALUES ('"
						+ way.id
						+ "','"						
						+ node.id
						+ "' );";
				stmt.executeUpdate(insertRoadPlace);
				
			}
		}
		stmt.close();
	}

	public static void main(String[] args) {
		LocationGenerator parser = new LocationGenerator();
		
		Connection connection = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:data/schema/astarDB-1");
			connection.setAutoCommit(false);
			System.out.println("Open database successfully");
			
			parser.addPlace(connection.createStatement());
			System.out.println("addPlace Records created successfully");
			
			parser.addRoad(connection.createStatement());
			System.out.println("addRoad Records created successfully");
			
			parser.addRoadPlace(connection.createStatement());
			System.out.println("addRoadPlace Records created successfully");
			
			connection.commit();
			connection.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Records created successfully");
		
	}
}
