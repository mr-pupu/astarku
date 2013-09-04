package id.ac.itats.skripsi.shortestpath.engine;

import id.ac.itats.skripsi.shortestpath.model.Vertex;


public interface AStarHeuristic {

	public double calcHeuristic(Vertex source, Vertex target);
}
