package id.ac.itats.skripsi.shortestpath.model;

public class Edge {
	public final Vertex target;
	public final double weight;
	
	public final String id;

	public Edge(String id, Vertex target, double weight) {
		this.target = target;
		this.weight = weight;
		this.id = id;
	}
}
