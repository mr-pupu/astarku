package id.ac.itats.skripsi.shortestpath.engine;

import id.ac.itats.skripsi.shortestpath.model.Edge;
import id.ac.itats.skripsi.shortestpath.model.Graph;
import id.ac.itats.skripsi.shortestpath.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class AStar2 {

	private double tLat;
	private double tLon;
	private Graph graph;

	public AStar2(Graph graph) {
		this.graph = graph;
	}

	public List<Vertex> computePaths(Vertex source, Vertex target) {
		if (!graph.hasClearTree) {
			graph.clearTree();
		}

		tLat = Double.valueOf(target.lat);
		tLon = Double.valueOf(target.lon);

		PriorityQueue<Vertex> openList = new PriorityQueue<Vertex>(5,
				Vertex.CompareF);

		source.minDistance = 0.;
		source.minF = source.minDistance + calcHeuristic(source);

		openList.add(source);

		while (!openList.isEmpty()) {
			Vertex current = openList.poll();
			current.onClosedList = true;

			if (current.equals(target)) {

				return reconstructPath(current);
			}

			// currentNeighborhood

			for (Edge e : current.adjacencies) {

				Vertex neighbor = e.target;
				boolean neighborIsBetter;

				if (neighbor.onClosedList) {
					continue;
				}

				if (!neighbor.isObstacle) {
					double tentativeG = current.minDistance + e.weight;

					if (!neighbor.onOpenList) {
						openList.add(neighbor);
						neighbor.onOpenList = true;
						neighborIsBetter = true;
					} else if (tentativeG < current.minDistance) {
						neighborIsBetter = true;
					} else {
						neighborIsBetter = false;
					}

					if (neighborIsBetter) {
						neighbor.previous = current;
						neighbor.minDistance = tentativeG;
						neighbor.minF = neighbor.minDistance
								+ calcHeuristic(neighbor);
					}
				}
			}
		}

		return null;
	}

	private List<Vertex> reconstructPath(Vertex target) {
		List<Vertex> path = new ArrayList<Vertex>();

		for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
			path.add(vertex);

		Collections.reverse(path);
		graph.hasClearTree = false;
		return path;

	}

	private double calcHeuristic(Vertex current) {

		double x = Double.valueOf(current.lat) - tLat;
		double y = Double.valueOf(current.lon) - tLon;

		return Math.sqrt(x * x + y * y);

	}

}
