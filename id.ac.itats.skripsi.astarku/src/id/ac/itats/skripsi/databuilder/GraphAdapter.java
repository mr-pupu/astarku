package id.ac.itats.skripsi.databuilder;

import id.ac.itats.skripsi.orm.DaoSession;
import id.ac.itats.skripsi.orm.Node;
import id.ac.itats.skripsi.orm.NodeDao;
import id.ac.itats.skripsi.orm.Way;
import id.ac.itats.skripsi.orm.WayDao;
import id.ac.itats.skripsi.shortestpath.model.Edge;
import id.ac.itats.skripsi.shortestpath.model.Graph;
import id.ac.itats.skripsi.shortestpath.model.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphAdapter {
	protected static final String TAG = "GraphAdapter";
	private static DaoSession daoSession = DataBaseHelper.getInstance().openSession();
	private static NodeDao nodeDao = daoSession.getNodeDao();
	private static WayDao wayDao = daoSession.getWayDao();

	private static List<Edge> edges = new ArrayList<Edge>();
	private static HashMap<Long, Vertex> vertices = new HashMap<Long, Vertex>();
	public static Graph graph;

	public static Node getNode(long nodeId) {
		return nodeDao.queryBuilder()
				.where(NodeDao.Properties.NodeID.eq(nodeId)).list().get(0);
	}

	public static void buildGraph() {
		String wayId = WayDao.Properties.Id.columnName;
		// int rowCount = mDb.rawQuery("SELECT "+ wayId +" FROM WAY",
		// null).getCount();
		int rowCount = 53702;

		int i = 0;
		int j = 1000;
		do {
			List<Way> ways = wayDao.queryDeep("WHERE T." + wayId
					+ " BETWEEN ? AND ?  ", new String[] { "" + i, "" + j });
			for (Way way : ways) {
				addEdge(way.getWayID(), way.getFk_sourceNode(), way
						.getSourceNode().getLatitude(), way.getSourceNode()
						.getLongitude(), way.getFk_targetNode(), way
						.getTargetNode().getLatitude(), way.getTargetNode()
						.getLongitude(), way.getWeight());
			}
			i = j + 1;
			j += 1000;

		} while (j <= rowCount);

		List<Way> ways = wayDao.queryDeep("WHERE T." + wayId + ">?", ""
				+ (j - 1000));
		for (Way way : ways) {
			addEdge(way.getWayID(), way.getFk_sourceNode(), way
					.getSourceNode().getLatitude(), way.getSourceNode()
					.getLongitude(), way.getFk_targetNode(), way
					.getTargetNode().getLatitude(), way.getTargetNode()
					.getLongitude(), way.getWeight());
		}
		graph  = new Graph(vertices, edges);
				
	}
		
	private static void addEdge(String edgeId, long fromNode, String fromLat, String fromLon, long toNode, String toLat, String toLon, double weight) {
		Vertex fromVertex = vertices.get(fromNode);
		if (fromVertex == null) {
			fromVertex = new Vertex(fromNode, fromLat, fromLon);
			vertices.put(fromNode, fromVertex);
		}
		Vertex toVertex = vertices.get(toNode);
		if (toVertex == null) {
			toVertex = new Vertex(toNode, toLat, toLon);
			vertices.put(toNode, toVertex);
		}
		Edge edge = new Edge(edgeId, toVertex, weight);
		fromVertex.adjacencies.add(edge);
		edges.add(edge);
	}

	public static Graph getGraph() {
		return graph;
	}
	
}
