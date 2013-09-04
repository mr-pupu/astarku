package id.ac.itats.skripsi.astarku.processor;

import id.ac.itats.skripsi.shortestpath.model.Vertex;
import id.ac.itats.skripsi.util.LatLongUtil;

import java.util.Collection;


public class MapMatchingUtil {

	public static Vertex doMatching(Collection<Vertex> vertexs, double gpsLat,
			double gpsLon) {
		Vertex minNode = null;

		double minJarak = Double.MAX_VALUE;
		for (Vertex vertex : vertexs) {


			double jarak = LatLongUtil.distance(gpsLat, gpsLon, Double.parseDouble(vertex.lat),
					Double.parseDouble(vertex.lon));

			// System.out.println(jarak);

			if (jarak < minJarak) {
				minJarak = jarak;
				minNode = vertex;
			}

		}

		return minNode;
	}

}
