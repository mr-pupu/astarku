package id.ac.itats.skripsi.orm.dbgenerator;


import id.ac.itats.skripsi.orm.generator.builder.Graph;
import id.ac.itats.skripsi.orm.generator.builder.Graph.Edge;
import id.ac.itats.skripsi.orm.generator.builder.Graph.Vertex;
import id.ac.itats.skripsi.orm.generator.builder.GraphBuilder;
import id.ac.itats.skripsi.orm.generator.parser.model.OSMNode;
import id.ac.itats.skripsi.orm.generator.parser.model.Way;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class GraphGenerator {

	public static void main(String[] args) throws Exception {
		Connection c = null;
		Statement stmt = null;

		GraphBuilder builder = new GraphBuilder("data/osm/surabaya_valid.osm");
		Graph graph = builder.getGraph();

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager
					.getConnection("jdbc:sqlite:data/schema/astarDB-1");
			c.setAutoCommit(false);
			System.out.println("Open database successfully");
			stmt = c.createStatement();

			// INSERT VERTEX
			for (Vertex vertex : graph.vertices.values()) {
				OSMNode node = vertex.node;
				
//				"CREATE TABLE " + constraint + "'NODE' (" + //
//                "'NODE_ID' INTEGER PRIMARY KEY ," + // 0: nodeID
//                "'LATITUDE' TEXT," + // 1: latitude
//                "'LONGITUDE' TEXT);"); // 2: longitude
				
				String sqlInsertNode = "INSERT INTO NODE (NODE_ID, LATITUDE, LONGITUDE) "
						+ "VALUES ("
						+ node.id
						+ ", "
						+ node.lat
						+ ", "
						+ node.lon
						+ ");";
				stmt.executeUpdate(sqlInsertNode);

				// INSERT EDGE

//				"CREATE TABLE " + constraint + "'WAY' (" + //
//                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
//                "'WAY_ID' TEXT," + // 1: wayID
//                "'FK_SOURCE_NODE' INTEGER," + // 2: fk_sourceNode
//                "'FK_TARGET_NODE' INTEGER," + // 3: fk_targetNode
//                "'WEIGHT' REAL);"); // 4: weight
				
				for (Edge edge : vertex.adjacencies) {
					Way way = edge.way;
					String sqlInsertEdge = "INSERT INTO WAY (WAY_ID, FK_SOURCE_NODE, FK_TARGET_NODE, WEIGHT)"
							+ " VALUES ("
							+ way.id
							+ ","
							+ node.id
							+ ","
							+ edge.toVertex.node.id
							+ "," + edge.weight
							+ " );";
					stmt.executeUpdate(sqlInsertEdge);

				}

			}

			stmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Records created successfully");

	}
}
