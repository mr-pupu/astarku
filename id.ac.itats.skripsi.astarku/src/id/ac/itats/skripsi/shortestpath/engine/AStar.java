package id.ac.itats.skripsi.shortestpath.engine;

import id.ac.itats.skripsi.shortestpath.model.Edge;
import id.ac.itats.skripsi.shortestpath.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class AStar {

	private Vertex target;
	private	double tLat, tLon;
	
	PriorityQueue<Vertex> openList = new PriorityQueue<Vertex>(5, Vertex.CompareF);
	
	public AStar(Vertex source, Vertex target){
		this.target = target;
		
		this.tLat = Double.valueOf(target.lat);
		this.tLon = Double.valueOf(target.lon);
		
		computePaths(source, target);
		
	}
	
	private void computePaths(Vertex source, Vertex target) {
	
		source.minDistance = 0.;
		source.minF = source.minDistance
				+ calcHeuristic(source);

		openList.add(source);
		
		while (!openList.isEmpty()) {
			Vertex current = openList.poll();
			current.onClosedList = true;
		
			// currentNeighborhood
			for (Edge e : current.adjacencies) {
				Vertex next = e.target;

				double tentativeG = current.minDistance + e.weight;

				if (next.onClosedList && tentativeG >= next.minDistance) {
					continue;
				}
				if (!next.onOpenList || tentativeG < next.minDistance) {
					next.previous = current;
					next.minDistance = tentativeG;
					next.minF = next.minDistance + calcHeuristic(next);
					openList.add(next);
					next.onOpenList = true;
				}

			}
		}
	}

	
	public List<Vertex> getShortestPath() {
		List<Vertex> path = new ArrayList<Vertex>();
		for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
			path.add(vertex);

		Collections.reverse(path);

		return path;
	}
	

	private double calcHeuristic(Vertex current) { 
		
		double x = Double.valueOf(current.lat) - tLat;
		double y = Double.valueOf(current.lon) - tLon;
	
		return Math.sqrt(x * x + y * y);
		
		
	}

}
