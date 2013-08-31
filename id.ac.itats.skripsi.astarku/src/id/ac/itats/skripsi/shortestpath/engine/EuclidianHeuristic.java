package id.ac.itats.skripsi.shortestpath.engine;

import id.ac.itats.skripsi.shortestpath.model.Vertex;

public class EuclidianHeuristic implements AStarHeuristic {

	@Override
	public double calcHeuristic(Vertex source, Vertex target) {
		
//		double x = source.point.getX() - target.point.getX();
//		double y = source.point.getY() - target.point.getY();

//		return Math.sqrt(x * x + y * y);
		
		return 0.;

	}

}
