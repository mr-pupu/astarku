package id.ac.itats.skripsi.shortestpath.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class Graph {
	private HashMap<Long, Vertex> vertices;
	private List<Edge> edges;
	public boolean hasClearTree = true;
	
	public Graph(HashMap<Long, Vertex> vertices, List<Edge> edges) {
		super();
		this.vertices = vertices;
		this.edges = edges;
	}

	public HashMap<Long, Vertex> getVertices() {
		return vertices;
	}

	public void setVertices(HashMap<Long, Vertex> vertices) {
		this.vertices = vertices;
	}
	
	public Collection<Vertex> getVerticeValues() {
		return vertices.values();
	}
	
	public Vertex getVertex(long id) {

		return vertices.get(id);
	}
	
	public List<Edge> getEdges() {

		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	public int getSize() {
		return vertices.size();
	}
	
	public void clear() {
		for(Vertex v: vertices.values()) {
			v.previous = null;
			v.adjacencies.clear();
		}
		vertices.clear();
	}
	
    public void clearTree()
    {
        for (Vertex v : vertices.values()) {
        	v.minDistance = Double.POSITIVE_INFINITY;
        	v.previous = null;
        	v.onOpenList = false;
        	v.onClosedList = false;
        	v.isObstacle = false;
        }

    }

}
