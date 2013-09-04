package id.ac.itats.skripsi.orm.schemagenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class SchemaGenerator {
	public static void main(String[] args) throws Exception {
		Schema schema = new Schema(1, "id.ac.itats.skripsi.orm");
		addGraph(schema);
		addLocation(schema);

		
	
		
		new DaoGenerator().generateAll(schema, "../SchemaGenerator-5.1/src-gen");
	}

	private static void addGraph(Schema schema) {
		Entity node = schema.addEntity("Node");
		node.setTableName("NODE");
		node.addLongProperty("nodeID").index().primaryKey();
		node.addStringProperty("latitude");
		node.addStringProperty("longitude");
		
		Entity way = schema.addEntity("Way");
		way.setTableName("WAY");
		way.addIdProperty().autoincrement();
		way.addStringProperty("wayID").index();
				
		Property sourceNode = way.addLongProperty("fk_sourceNode").getProperty(); //fk_sourceNode		
		way.addToOne(node, sourceNode, "sourceNode");	//many (edge) TO one (sourceNode) 
		
		Property targetNode = way.addLongProperty("fk_targetNode").getProperty(); //fk_targetNode
		way.addToOne(node, targetNode, "targetNode"); //many (edge) TO one (targetNodeNode)
		
		way.addDoubleProperty("weight");
		
		node.addToMany(way, sourceNode, "sourceAdjacencies"); // one (sourceNode) TO many (edge)
		node.addToMany(way, targetNode, "targetAdjacencies"); // one (targetNode) TO many (edge)
		
		
	}
	
	private static void addLocation(Schema schema) {
		Entity place = schema.addEntity("Place");
		place.setTableName("PLACE");
		Property placeId = place.addLongProperty("placeID").primaryKey().getProperty();
		place.addStringProperty("latLon");
		Property category = place.addStringProperty("category").getProperty();
		Property placeName = place.addStringProperty("name").getProperty();
		place.addStringProperty("address");
		
		Index indexPlace = new Index();
		indexPlace.addProperty(placeId);
		indexPlace.addProperty(category);
		indexPlace.addProperty(placeName);
		place.addIndex(indexPlace);
		
		Entity road = schema.addEntity("Road");
		road.setTableName("ROAD");
		Property roadId = road.addLongProperty("roadID").primaryKey().getProperty();
		Property roadName = road.addStringProperty("name").getProperty();
		Index indexRoad = new Index();
		indexRoad.addProperty(roadId);
		indexRoad.addProperty(roadName);
		road.addIndex(indexRoad);	
		
		Entity roadPlace = schema.addEntity("RoadPlace");
		roadPlace.addIdProperty().autoincrement();		
		roadPlace.addLongProperty("fk_road");
		roadPlace.addLongProperty("fk_place");		
		
	}

}
