/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package id.ac.itats.skripsi.orm.generator.parser.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKBWriter;
import java.util.Map;

/**
 *
 * @author Willy Tiengo
 */
public class OSMNode extends AbstractNode {

    public String lat;
    public String lon;

    public OSMNode(String id, String visible, String timestamp, String version, String changeset, String user, String uid, String lat, String lon, Map<String, String> tags) {
        super(id, visible, timestamp, version, changeset, user, uid, tags);
        this.lat = lat;
        this.lon = lon;
        this.tags = tags;
    }



	public String getLocation() {
        Point p = new GeometryFactory().createPoint(
                new Coordinate(Double.valueOf(lon), Double.valueOf(lat)));

        return WKBWriter.toHex(new WKBWriter().write(p));
    }
    
    //Added by Joris Maervoet, KaHoSL
    public Point getPoint() {
        Point p = new GeometryFactory().createPoint(
                new Coordinate(Double.valueOf(lon), Double.valueOf(lat)));

        return p;
    }


    // new add

	@Override
	public String toString() {
		return "OSMNode [lat=" + lat + ", lon=" + lon + ", id=" + id + ", tags=" + tags + "]";
	}

    
    
}
