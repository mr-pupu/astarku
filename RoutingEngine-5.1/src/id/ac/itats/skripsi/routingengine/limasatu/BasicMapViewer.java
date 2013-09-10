package id.ac.itats.skripsi.routingengine.limasatu;

import id.ac.itats.skripsi.databuilder.GraphAdapter;
import id.ac.itats.skripsi.shortestpath.engine.AStar2;
import id.ac.itats.skripsi.shortestpath.engine.Dijkstra;
import id.ac.itats.skripsi.shortestpath.model.Graph;
import id.ac.itats.skripsi.shortestpath.model.Vertex;
import id.ac.itats.skripsi.util.MapMatchingUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.android.AndroidPreferences;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.common.PreferencesFacade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BasicMapViewer extends SherlockFragmentActivity {
	protected final String TAG = BasicMapViewer.class.getSimpleName();
	protected MapView mapView;
	protected PreferencesFacade preferencesFacade;
	protected TileCache tileCache;

	private GestureDetector gestureDetector;
	private LayerManager layerManager;
	private volatile boolean shortestPathRunning = false;
	private LatLong start, end;
	private List<Layer> layersOverlay = new ArrayList<Layer>();


	protected SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onSingleTapConfirmed(android.view.MotionEvent motionEvent) {

			MapViewPosition mapPosition = mapView.getModel().mapViewPosition;
			LatLong geoPoint = mapPosition.getMapPosition().latLong;

			if (shortestPathRunning) {
				logUser("Calculation still in progress");
				return false;
			}
			
			if (GraphAdapter.getGraph() == null) {
				logUser("Graph not yet ready!");
				return false;
			}
			
			float x = motionEvent.getX();
			float y = motionEvent.getY();

			double pixelX = MercatorProjection.longitudeToPixelX(
					geoPoint.longitude, mapPosition.getZoomLevel());
			double pixelY = MercatorProjection.latitudeToPixelY(
					geoPoint.latitude, mapPosition.getZoomLevel());

			pixelX -= mapView.getWidth() >> 1;
			pixelY -= mapView.getHeight() >> 1;

			LatLong tmpPoint = new LatLong(MercatorProjection.pixelYToLatitude(
					pixelY + y, mapPosition.getZoomLevel()),
					MercatorProjection.pixelXToLongitude(pixelX + x,
							mapPosition.getZoomLevel()));
			if (start != null && end == null) {
				end = tmpPoint;
				shortestPathRunning = true;

				Marker marker = Utils.createMarker(BasicMapViewer.this,
						R.drawable.marker_red, tmpPoint);
				layersOverlay.add(marker);
				if (marker != null) {
					layerManager.getLayers().add(marker);
					layerManager.redrawLayers();
				}
				calcPath(start.latitude, start.longitude, end.latitude,
						end.longitude);
			
				
			} else {
				start = tmpPoint;
				end = null;

				for (Layer layer : layersOverlay) {
					layerManager.getLayers().remove(layer);
				}

				Marker marker = Utils.createMarker(BasicMapViewer.this,
						R.drawable.marker_green, start);
				
				layersOverlay.add(marker);

				if (marker != null) {
					layerManager.getLayers().add(marker);
					layerManager.redrawLayers();
				}
			}

			return true;
		}
	};

	protected void addLayers(LayerManager layerManager, TileCache tileCache,
			MapViewPosition mapViewPosition) {
		layerManager.getLayers().add(
				Utils.createTileRendererLayer(tileCache, mapViewPosition,
						getMapFile()));
	}

	protected TileCache createTileCache() {
		return Utils.createExternalStorageTileCache(this, getPersistableId());
	}

	protected MapPosition getInitialPosition() {
		return new MapPosition(new LatLong(-7.2517722, 112.6822205), (byte) 14);
	}

	protected File getMapFile() {
		return new File(Environment.getExternalStorageDirectory(),
				this.getMapFileName());
	}

	protected String getMapFileName() {
		return "maps/surabaya_new.map";
	}

	protected MapView getMapView() {

		return new MapView(this) {
			@Override
			public boolean onTouchEvent(MotionEvent motionEvent) {
				if (gestureDetector.onTouchEvent(motionEvent)) {
					return true;
				}
				return super.onTouchEvent(motionEvent);
			}
		};
	}

	protected String getPersistableId() {
		return this.getClass().getSimpleName();
	}

	protected void init() {
		this.mapView = getMapView();
		
		layerManager = mapView.getLayerManager();

		gestureDetector = new GestureDetector(this, gestureListener);

		initializeMapView(this.mapView, this.preferencesFacade);

		this.tileCache = createTileCache();

		MapViewPosition mapViewPosition = this.initializePosition(this.mapView
				.getModel().mapViewPosition);

		addLayers(this.mapView.getLayerManager(), this.tileCache,
				mapViewPosition);
		setContentView();
	}

	protected void initializeMapView(MapView mapView,
			PreferencesFacade preferences) {
		mapView.getModel().init(preferences);
		mapView.setClickable(true);
		mapView.getMapScaleBar().setVisible(false);
	}

	
	
	protected MapViewPosition initializePosition(MapViewPosition mapViewPosition) {
		LatLong center = mapViewPosition.getCenter();
		Log.i(TAG, "" + center);
		if (center.equals(new LatLong(0, 0))) {
			mapViewPosition.setMapPosition(this.getInitialPosition());
		}
		return mapViewPosition;
	}



	protected void addMarker(MotionEvent motionEvent) {
		Marker marker = null;
		MapViewPosition mapPosition = mapView.getModel().mapViewPosition;
		LatLong geoPoint = mapPosition.getMapPosition().latLong;

		float x = motionEvent.getX();
		float y = motionEvent.getY();

		double pixelX = MercatorProjection.longitudeToPixelX(
				geoPoint.longitude, mapPosition.getZoomLevel());
		double pixelY = MercatorProjection.latitudeToPixelY(geoPoint.latitude,
				mapPosition.getZoomLevel());

		pixelX -= mapView.getWidth() >> 1;
		pixelY -= mapView.getHeight() >> 1;

		LatLong latLong = new LatLong(MercatorProjection.pixelYToLatitude(
				pixelY + y, mapPosition.getZoomLevel()),
				MercatorProjection.pixelXToLongitude(pixelX + x,
						mapPosition.getZoomLevel()));
		marker = Utils.createMarker(this, R.drawable.marker_red, latLong);
		this.mapView.getLayerManager().getLayers().add(marker);
		this.mapView.getLayerManager().redrawLayers();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent service = new Intent(BasicMapViewer.this, GraphService.class);
		
		startService(service);
		
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				getPersistableId(), MODE_PRIVATE);
		this.preferencesFacade = new AndroidPreferences(sharedPreferences);

		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		this.mapView.destroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.mapView.getModel().save(this.preferencesFacade);
		this.preferencesFacade.save();
	}

	protected void setContentView() {
		setContentView(this.mapView);
	}

	protected void logUser(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}

	protected void log(String str) {
		Log.i(TAG, str);
	}
	
	//XXX calcpath
	protected void calcPath(final double fromLat, final double fromLon,
			final double toLat, final double toLon) {
		new AsyncTask<Void, Void, List<Vertex>>() {

			Graph graph = GraphAdapter.getGraph();
			
			@Override
			protected List<Vertex> doInBackground(Void... params) {
				Vertex source = MapMatchingUtil.doMatching(
						graph.getVerticeValues(), fromLat, fromLon);

				System.out.println(source.id);

				Vertex target = MapMatchingUtil.doMatching(
						graph.getVerticeValues(), toLat, toLon);

				System.out.println(target.id);

				AStar2 aStar2 = new AStar2(graph);
				List<Vertex> path = aStar2.computePaths(source, target);
				


				System.out.println(path);
				
				return path;
			}

			@Override
			protected void onPostExecute(List<Vertex> result) {
					
				shortestPathRunning = false;

				if(result != null) {
					Polyline polyline = new Polyline(Utils.createPaint(
							AndroidGraphicFactory.INSTANCE
									.createColor(Color.BLUE), 7, Style.STROKE),
							AndroidGraphicFactory.INSTANCE);
					List<LatLong> latLongs = polyline.getLatLongs();

					for (Vertex vertex : result) {
						latLongs.add(new LatLong(
								Double.parseDouble(vertex.lat), Double
										.parseDouble(vertex.lon)));
					}

					layerManager.getLayers().add(polyline);
					layerManager.redrawLayers();

					layersOverlay.add(polyline);
					logUser("Shortest path finish...");
				} else {
					logUser("Sorry path not found...");
					for (Layer layer : layersOverlay) {
						layerManager.getLayers().remove(layer);
					}
					
				}

			}
		}.execute();
	}

	
}
