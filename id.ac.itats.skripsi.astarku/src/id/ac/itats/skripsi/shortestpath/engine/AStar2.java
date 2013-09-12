package id.ac.itats.skripsi.shortestpath.engine;

import id.ac.itats.skripsi.astarku.processor.ProgressReporter;
import id.ac.itats.skripsi.shortestpath.model.Edge;
import id.ac.itats.skripsi.shortestpath.model.Graph;
import id.ac.itats.skripsi.shortestpath.model.Vertex;
import id.ac.itats.skripsi.util.LatLongUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import com.vividsolutions.jts.geom.Point;

public class AStar2 {

	private Point targetPoint;
	private Graph graph;
	private ProgressReporter reporter;

	public AStar2(Graph graph, ProgressReporter reporter) {
		this.graph = graph;
		this.reporter = reporter;
	}

	public List<Vertex> computePaths(Vertex source, Vertex target) {

		int process = 0;
		reporter.finish(false);

		if (!graph.hasClearTree) {
			graph.clearTree();
		}

		if (reporter.getObstacleList() != null) {
			for (Vertex obstacle : reporter.getObstacleList()) {
				obstacle.isObstacle = true;
			}
		}

		reporter.process(process);

		targetPoint = LatLongUtil.getPoint(target.lat, target.lon);

		PriorityQueue<Vertex> openList = new PriorityQueue<Vertex>(5, Vertex.CompareF);

		source.minDistance = 0.;
		source.minF = source.minDistance + calcHeuristic(source);

		openList.add(source);

		while (!openList.isEmpty()) {
			reporter.process(process++);
			Vertex current = openList.poll();
			current.onClosedList = true;

			if (current.equals(target)) {
				reporter.report("Shortest path finish...!");
				reporter.finish(true);
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
						neighbor.minF = neighbor.minDistance + calcHeuristic(neighbor);
					}
				}
			}
		}

		reporter.report("path not found!");
		reporter.finish(true);
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

		double x = LatLongUtil.getPoint(current.lat, current.lon).getX() - targetPoint.getX();
		double y = LatLongUtil.getPoint(current.lat, current.lon).getY() - targetPoint.getY();
		return Math.sqrt(x * x + y * y);

	}

	public static String[] printPath(List<Vertex> path) {
		String[] result = new String[path.size() + 1];
		double total = 0;
		Vertex prev = null;
		int i = 0;
		for (Vertex v : path) {
			if (prev != null) {
				for (Edge e : prev.adjacencies) {
					if (e.target == v) {
						result[i] = e.id + "," + Double.toString(e.weight).substring(0,2); //m

					}
				}
				total = v.minDistance;
			}
			prev = v;
			i++;
		}
		result[i] = "" + Double.toString(total/1000).substring(0,3); //km
		return result;
	}
	
	/*
	 * 	public static String[] printPath(List<Vertex> path) {
		String[] result = new String[path.size() + 1];
		double total = 0;
		Vertex prev = null;
		int i = 0;
		for (Vertex v : path) {
			if (prev != null) {
				for (Edge e : prev.adjacencies) {
					if (e.target == v) {
						result[i] = e.id + " || " + Math.round(e.weight*1000);

					}
				}
				total = v.minDistance;
			}
			prev = v;
			i++;
		}
		result[i] = "" + threeDigits(total);
		return result;
	}
	 */

}
