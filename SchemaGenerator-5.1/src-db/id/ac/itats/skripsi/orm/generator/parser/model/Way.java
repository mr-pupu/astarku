/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.itats.skripsi.orm.generator.parser.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.io.WKBWriter;

import id.ac.itats.skripsi.orm.generator.tools.parser.util.LatLongUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Willy Tiengo
 */
public class Way extends AbstractNode {

	// Constants ---------------------------------------------------------------
	public static final String HIGHWAY = "highway";
	// Attributes
	// ---------------------------------------------------------------
	public List<OSMNode> nodes;

	// Getter added by Joris Maervoet, KaHoSL
	public List<OSMNode> getNodes() {
		return nodes;
	}

	public Way(String id, String visible, String timestamp, String version,
			String changeset, String user, String uid, List<OSMNode> nodes,
			Map<String, String> tags) {

		super(id, visible, timestamp, version, changeset, user, uid, tags);
		this.nodes = nodes;
	}

	public LineString getLineString() {
		List<Coordinate> coords = new ArrayList<Coordinate>();
		GeometryFactory fac = new GeometryFactory();

		Coordinate c1;
		for (OSMNode node : nodes) {
			c1 = new Coordinate(Double.parseDouble(node.lon),
					Double.parseDouble(node.lat));
			coords.add(c1);
		}

		return fac.createLineString(coords.toArray(new Coordinate[0]));
	}

	public boolean isHighway() {
		return (tags.get(HIGHWAY) != null);
	}

	// Removed by Joris Maervoet, KaHoSL
	/*
	 * public boolean isOneway() { String oneway = tags.get("oneway");
	 * 
	 * return ((oneway != null) ? oneway.equals("yes") : false);
	 * 
	 * }
	 */

	// Added by Joris Maervoet, KaHoSL
	public int getOnewayDirection() {
		String oneway = tags.get("oneway");
		if (oneway != null) {
			if ((oneway.equals("yes")) || (oneway.equals("true"))
					|| (oneway.equals("1"))) {
				return 1;
			}
			if (oneway.equals("-1")) {
				return -1;
			}
			if ((oneway.equals("no")) || (oneway.equals("false"))
					|| (oneway.equals("0"))) {
				return 0;
			}
		}
		String junction = tags.get("junction");
		if ((junction != null) && (junction.equals("roundabout"))) {
			return 1;
		}
		String highway = tags.get("highway");
		if ((highway != null) && (highway.equals("motorway_link"))) {
			return 1;
		}
		return 0;
	}

	// Added by Joris Maervoet, KaHoSL
	public boolean isAccessibleByCar() {
		String highway = tags.get("highway");
		if (highway != null) {
			if ((highway.equals("bridleway"))
					|| (highway.equals("bus_guideway"))
					|| (highway.equals("construction"))
					|| (highway.equals("cycleway"))
					|| (highway.equals("footway")) 
					|| (highway.equals("path"))
					|| (highway.equals("pedestrian"))
					|| (highway.equals("proposed"))
					|| (highway.equals("raceway"))
					|| (highway.equals("service")) 
					|| (highway.equals("steps"))) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	// Added by Joris Maervoet, KaHoSL
	public String getMaxSpeed() {
		return tags.get("maxspeed");
	}

	public String getName() {
		return tags.get("name");
	}

	public String getWayMiddle() {
		double lenMiddle, distance, lineDistance;
		GeometryFactory fac = new GeometryFactory();

		OSMNode n1 = null, n2 = null;

		lenMiddle = wayLength(nodes) / 2;
		distance = 0d;

		for (int i = 0; i < nodes.size() - 1; i++) {

			n1 = nodes.get(i);
			n2 = nodes.get(i + 1);

			lineDistance = lineDistance(n1, n2);

			if ((distance + lineDistance) > lenMiddle) {
				distance = (lenMiddle - distance) / lineDistance;
				break;
			}

			distance += lineDistance;
		}

		double lat = Double.parseDouble(n2.lat);
		double lon = Double.parseDouble(n2.lon);

		if (distance > 0.0d) {
			distance = (1 / distance);

			// Baseado na prova do ponto médio
			lat = (Double.parseDouble(n2.lat) + (distance - 1)
					* Double.parseDouble(n1.lat))
					/ distance;
			lon = (Double.parseDouble(n2.lon) + (distance - 1)
					* Double.parseDouble(n1.lon))
					/ distance;
		}

		return WKBWriter.toHex(new WKBWriter().write(fac
				.createPoint(new Coordinate(lon, lat))));
	}

	public double getWayLength() {
		return wayLength(nodes);
	}

	// Added by Joris Maervoet, KaHoSL
	public double getWayPartLength(int fromIndex, int toIndex) {

		return wayLength(nodes.subList(fromIndex, toIndex));
	}

	public String getType() {
		return tags.get(HIGHWAY);
	}

	public String getShape() throws Exception {
		MultiLineString mls;

		// Precisa ser um MultiLineString
		mls = new GeometryFactory()
				.createMultiLineString(new LineString[] { getLineString() });

		return WKBWriter.toHex(new WKBWriter().write(mls));
	}

	public String getAltNames() {
		return tags.get("alt_name");
	}

	// Private methods ---------------------------------------------------------
	private double wayLength(List<OSMNode> nodes) {
		double length = 0d;
		OSMNode n1, n2;

		n1 = nodes.get(0);

		for (int i = 1; i < nodes.size(); i++) {
			n2 = nodes.get(i);

			length += LatLongUtil.distance(Double.parseDouble(n1.lat),
					Double.parseDouble(n1.lon), Double.parseDouble(n2.lat),
					Double.parseDouble(n2.lon));

			n1 = n2;
		}

		return length;
	}

	private static Double lineDistance(OSMNode n1, OSMNode n2) {

		return LatLongUtil.distance(Double.parseDouble(n1.lat),
				Double.parseDouble(n1.lon), Double.parseDouble(n2.lat),
				Double.parseDouble(n2.lon));

	}

	
	
	// new add
	@Override
	public String toString() {
		return "Way [nodes=" + nodes + ", id=" + id + ", tags=" + tags + "]";
	}
	
}
