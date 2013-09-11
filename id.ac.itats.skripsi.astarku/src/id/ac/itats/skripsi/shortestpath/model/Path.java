package id.ac.itats.skripsi.shortestpath.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapsforge.core.model.LatLong;

public class Path {
	
	public static class PathItem {
		public final String content;
		public final String id;
		public final LatLong location;
		public final String text;

		public PathItem(String id, String content, LatLong location, String text) {
			this.id = id;
			this.content = content;
			this.location = location;
			this.text = text;
		}

		@Override
		public String toString() {
			return this.content;
		}
	}

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static final Map<String, PathItem> ITEM_MAP = new HashMap<String, PathItem>();

	/**
	 * An array of sample (dummy) items.
	 */
	public static final List<PathItem> ITEMS = new ArrayList<PathItem>();

	static {
		addItem(new PathItem("1", "Brandenburger Tor", new LatLong(52.516, 13.378),
				"This is the famous Brandenburger Tor"));
		addItem(new PathItem("2", "Checkpoint Charlie", new LatLong(52.507, 13.390),
				"This used to be the famous Checkpoint Charlie"));
		addItem(new PathItem(
				"3",
				"Savigny Platz",
				new LatLong(52.505, 13.322),
				"This is a square in Berlin with a longer text that does not really say anything at all and you would see more of the map if this useless text was not here."));
	}

	private static void addItem(PathItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}
}
