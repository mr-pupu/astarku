package id.ac.itats.skripsi.shortestpath.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Vertex implements Comparable<Vertex> {
	public final long id;
	public final String lat, lon;
	
	public List<Edge> adjacencies = new ArrayList<Edge>();
	public double minDistance = Double.POSITIVE_INFINITY;
	public double minF = Double.POSITIVE_INFINITY;
	
	public Vertex previous;
	
	boolean goal;
	public boolean onOpenList;
	public boolean onClosedList;
	public boolean isObstacle;

	public Vertex(long argName, String argLat, String argLon) {
		id = argName;
		lat = argLat;
		lon = argLon;
		
		onOpenList = false;
		onClosedList = false;
		isObstacle = false;
	}

	@Override
	public String toString() {
		return "" + id;
	}

	@Override
	public int compareTo(Vertex other) {
		return Double.compare(this.minDistance, other.minDistance);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public boolean isGoal() {
		return goal;
	}

	public void setGoal(boolean b) {
		goal = b;
		
	}
	
	public static Comparator<Vertex> CompareF = new Comparator<Vertex>() {

		@Override
		public int compare(Vertex vertex1, Vertex vertex2) {
			Double f1 = vertex1.minDistance + vertex1.minF;
			Double f2 = vertex2.minDistance + vertex2.minF;
			
			return f1.compareTo(f2);
		}
		
	}; 

}
