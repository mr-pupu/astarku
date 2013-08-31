package id.ac.itats.skripsi.shortestpath.engine;

import id.ac.itats.skripsi.orm.Node;
import id.ac.itats.skripsi.orm.Way;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import android.util.Log;

public class DijkstraDB {
	protected static final String TAG = "Dijkstra";
	
	public static void computePaths(Node source) {
		
		source.minDistance = 0.;
		PriorityQueue<Node> vertexQueue = new PriorityQueue<Node>();
		vertexQueue.add(source);
		Log.e(TAG, "vertequeue size  "+vertexQueue.size());
		
		while (!vertexQueue.isEmpty()) {
			Node u = vertexQueue.poll();
			Log.i(TAG, "Node u : "+u.minDistance);
		
			
			for (Way e : u.getSourceAdjacencies()) {
				Log.i(TAG, "Way "+e.getWayID());
				
				Node v = e.getTargetNode();
				Log.i(TAG, "Node v : "+v.getNodeID());
				Log.i(TAG, "Node v : "+v.minDistance);
				
				
				double weight = e.getWeight();
				Log.i(TAG, "weight : "+weight);
				
				double distanceThroughU = u.minDistance + weight;
				Log.i(TAG, "distanceThroughU : "+distanceThroughU);
				
				Log.e(TAG,""+(distanceThroughU < v.minDistance));
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);

					v.minDistance = distanceThroughU;
					v.previous = u;
					vertexQueue.add(v);
					Log.e(TAG, "vertequeue size  "+vertexQueue.size());
				}
			}
		}
	}

	public static List<Node> getShortestPathTo(Node target) {
		List<Node> path = new ArrayList<Node>();
		for (Node vertex = target; vertex != null; vertex = vertex.previous)
			path.add(vertex);

		Collections.reverse(path);
		return path;
	}
}