package id.ac.itats.skripsi.orm.generator.builder;

import id.ac.itats.skripsi.orm.generator.parser.model.OSMNode;
import id.ac.itats.skripsi.orm.generator.parser.model.Way;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Graph {
	public List<Edge> edges = new LinkedList<>();
	public HashMap<String, Vertex> vertices = new HashMap<>();

	public void addEdge(Way way, OSMNode fromNode, OSMNode toNode, double jarak) {
		Vertex fromVertex = vertices.get(fromNode.id);
		if (fromVertex == null) {
			fromVertex = new Vertex(fromNode);
			vertices.put(fromNode.id, fromVertex);
		}
		Vertex toVertex = vertices.get(toNode.id);
		if (toVertex == null) {
			toVertex = new Vertex(toNode);
			vertices.put(toNode.id, toVertex);
		}
		Edge edge = new Edge(way, toVertex, jarak);
		fromVertex.adjacencies.add(edge);
		edges.add(edge);
	}

	public class Vertex {

		public final OSMNode node;
		public LinkedList<Edge> adjacencies = new LinkedList<Edge>();

		public Vertex(OSMNode node) {
			this.node = node;
		}
	}

	public class Edge {
		public final Way way;
		public final Vertex toVertex;
		public final Double weight;

		public Edge(Way way, Vertex toVertex, double jarak) {
			this.way = way;
			this.toVertex = toVertex;
			this.weight = jarak;
		}
	}

}