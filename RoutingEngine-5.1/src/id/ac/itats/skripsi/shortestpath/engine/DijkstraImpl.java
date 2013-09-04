package id.ac.itats.skripsi.shortestpath.engine;

import id.ac.itats.skripsi.shortestpath.model.Edge;
import id.ac.itats.skripsi.shortestpath.model.Graph;
import id.ac.itats.skripsi.shortestpath.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class DijkstraImpl{
	HashSet<Vertex> visited = new HashSet<Vertex>();
	
	HashMap<Vertex, Double> cost = new HashMap<Vertex, Double>();
	
	private Graph graph;
	
	
	public void getShortestPath(Graph graph, Vertex current) {
		this.graph = graph;
		visited.clear();
		
		cost.put(current, 0.);
		
		run(current);
		
	}

	protected void run(Vertex current) {
		DijkstraImpl : do {
			if(current.isGoal()){
				visited.add(current);
				break DijkstraImpl;
			}
			
			double currentDistance = cost(current);
			double distance;
			double distancePrev;
			
			if(current.adjacencies != null){
				for (Edge e : current.adjacencies){
					Vertex n = e.target;
					if (!visited.contains(n)) {
						distance = currentDistance + 1;
						distancePrev = cost(n);
						
						if(distance < distancePrev){
							cost.put(n, distance);
						}
					}
					visited.add(current);
					Vertex best = null;
					double bestDistance = Double.POSITIVE_INFINITY;
					double estimate;
					for (Vertex v : graph.getVerticeValues())
						if(!visited.contains(v)){
							estimate = cost(v) + h(v);
							if (estimate < bestDistance){
								best = n;
								bestDistance = estimate;
							}
						}
					current = best;
					
				}
			}
		} while (current != null);
		
	}

	protected double cost(Vertex vertex) {
		Double c = cost.get(vertex);
		return c == null ? Double.POSITIVE_INFINITY : c;
	}
	
	protected double h(Vertex vertex){
		return 0;
	}

	public List<Vertex> getVisited() {
		List<Vertex> path = new ArrayList<Vertex>();
		 Iterator<Vertex> it = visited.iterator();
		 while (it.hasNext()) {
			path.add(it.next());
		}
		 
		Collections.reverse(path);
		return path;
	}
	
	
}