package id.ac.itats.skripsi.astarku;

import id.ac.itats.skripsi.astarku.processor.AstarProcessor;
import id.ac.itats.skripsi.astarku.processor.MapMatchingUtil;
import id.ac.itats.skripsi.astarku.processor.ProcessListener;
import id.ac.itats.skripsi.astarku.processor.Reporter;
import id.ac.itats.skripsi.databuilder.GraphAdapter;
import id.ac.itats.skripsi.shortestpath.engine.AStar2;
import id.ac.itats.skripsi.shortestpath.model.Graph;
import id.ac.itats.skripsi.shortestpath.model.Vertex;
import id.ac.itats.skripsi.util.MapviewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.android.AndroidPreferences;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.layer.MyLocationOverlay;
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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BasicMapViewer extends SherlockFragmentActivity implements ProcessListener {
	private final String TAG = BasicMapViewer.class.getSimpleName();

	// Mapview
	protected String mapFile = "surabaya_new.map";

	protected MapView mapView;
	protected PreferencesFacade preferencesFacade;
	protected TileCache tileCache;
	protected MyLocationOverlay myLocationOverlay;
	protected GestureDetector gestureDetector;
	protected LayerManager layerManager;
	protected volatile boolean shortestPathRunning = false;
	protected LatLong start, end, touchLatLon;
	protected Marker startMarker, endMarker;
	protected Polyline polyline;
	protected List<Layer> layersOverlay = new ArrayList<Layer>();
	protected List<LatLong> obstacleList = new ArrayList<LatLong>();

	// Process
	protected volatile Graph graph;
	protected Reporter reporter;
	protected AstarProcessor astarProcessor;


	protected SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onSingleTapConfirmed(android.view.MotionEvent motionEvent) {

			graph = GraphAdapter.getGraph() != null ? GraphAdapter.getGraph() : null;

			if (graph == null) {
				AppUtil.logUser(BasicMapViewer.this, getString(R.string.astarku__graph_isnull));
				return false;
			}

			if (shortestPathRunning) {
				AppUtil.logUser(BasicMapViewer.this, getString(R.string.astarku__shortestPathRunning));
				return false;
			}

			LatLong tmpPoint = getTouchLatLon(motionEvent);

			if (start != null && end == null) {
				setEnd(tmpPoint);

				processAstar(start.latitude, start.longitude, end.latitude, end.longitude);
			} else {
				cleanLayerOverlay();
				obstacleList.clear();
				setStart(tmpPoint);
			}

			return true;
		}

		@Override
		public void onLongPress(MotionEvent motionEvent) {
			touchLatLon = getTouchLatLon(motionEvent);
			openContextMenu(mapView);
		}
	};

	protected void setStart(LatLong start) {
		this.start = start;
		end = null;
		// mapView.getModel().mapViewPosition.setCenter(start);

		startMarker = MapviewUtils.createMarker(BasicMapViewer.this, R.drawable.ic_marker_start, start);

		if (startMarker != null) {
			layersOverlay.add(startMarker);
			layerManager.getLayers().add(startMarker);
			layerManager.redrawLayers();
		}
	}

	protected void cleanLayerOverlay() {
		for (Layer layer : layersOverlay) {
			layerManager.getLayers().remove(layer);
		}
		layersOverlay.clear();
	}

	protected void setEnd(LatLong end) {
		this.end = end;
		shortestPathRunning = true;

		endMarker = MapviewUtils.createMarker(BasicMapViewer.this, R.drawable.ic_marker_end, end);

		if (endMarker != null) {
			layersOverlay.add(endMarker);
			layerManager.getLayers().add(endMarker);
			layerManager.redrawLayers();
		}

	}

	protected LatLong getTouchLatLon(MotionEvent motionEvent) {
		MapViewPosition mapPosition = mapView.getModel().mapViewPosition;
		LatLong geoPoint = mapPosition.getMapPosition().latLong;

		float x = motionEvent.getX();
		float y = motionEvent.getY();

		double pixelX = MercatorProjection.longitudeToPixelX(geoPoint.longitude, mapPosition.getZoomLevel());
		double pixelY = MercatorProjection.latitudeToPixelY(geoPoint.latitude, mapPosition.getZoomLevel());

		pixelX -= mapView.getWidth() >> 1;
		pixelY -= mapView.getHeight() >> 1;

		LatLong latLon = new LatLong(MercatorProjection.pixelYToLatitude(pixelY + y, mapPosition.getZoomLevel()),
				MercatorProjection.pixelXToLongitude(pixelX + x, mapPosition.getZoomLevel()));

		return latLon;
	}

	protected void addLayers(LayerManager layerManager, TileCache tileCache, MapViewPosition mapViewPosition) {
		layerManager.getLayers().add(MapviewUtils.createTileRendererLayer(tileCache, mapViewPosition, getMapFile()));

		Drawable drawable = getResources().getDrawable(R.drawable.person);
		Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);

		myLocationOverlay = new MyLocationOverlay(this, mapViewPosition, bitmap);
		myLocationOverlay.setSnapToLocationEnabled(true);

		layerManager.getLayers().add(myLocationOverlay);

	}

	protected TileCache createTileCache() {
		return MapviewUtils.createExternalStorageTileCache(this, getPersistableId());
	}

	protected MapPosition getInitialPosition() {
		return new MapPosition(new LatLong(-7.289936, 112.779097), (byte) 14);
	}

	protected File getMapFile() {
		return new File(Environment.getExternalStorageDirectory() + "/routingengine/maps/" + mapFile);
	}

	protected MapView getMapView() {

		return new MapView(this) {
			@Override
			public boolean onTouchEvent(MotionEvent motionEvent) {
				if (gestureDetector.onTouchEvent(motionEvent)) {
					return true;
				}

				if (!(motionEvent.getAction() == MotionEvent.ACTION_MOVE)) {
					gestureDetector.setIsLongpressEnabled(true);
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
		registerForContextMenu(mapView);

		layerManager = mapView.getLayerManager();

		gestureDetector = new GestureDetector(this, gestureListener);

		initializeMapView(this.mapView, this.preferencesFacade);

		this.tileCache = createTileCache();

		MapViewPosition mapViewPosition = this.initializePosition(this.mapView.getModel().mapViewPosition);

		addLayers(this.mapView.getLayerManager(), this.tileCache, mapViewPosition);


		setContentView();

	}

	protected void initializeMapView(MapView mapView, PreferencesFacade preferences) {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent service = new Intent(BasicMapViewer.this, GraphService.class);
		startService(service);

		SharedPreferences sharedPreferences = this.getSharedPreferences(getPersistableId(), MODE_PRIVATE);
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
		this.myLocationOverlay.disableMyLocation();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.myLocationOverlay.enableMyLocation(true);
		
		if(GraphAdapter.getGraph()==null){
			Intent service = new Intent(BasicMapViewer.this, GraphService.class);
			startService(service);
			
		}
	}

	protected void setContentView() {
		setContentView(this.mapView);
	}

	// TODO ROUTING
	protected void processAstar(double fromLat, double fromLon, double toLat, double toLon) {
		if (reporter == null) {
			reporter = new Reporter();
		}
		astarProcessor = new AstarProcessor(this, reporter);
		setSupportProgressBarVisibility(true);
		if (progress == 100) {
			progress = 0;
			progressRunner.run();
		}
		astarProcessor.execute(fromLat, fromLon, toLat, toLon);
	}

	@Override
	public void onPreprocess() {
		if (obstacleList.size() > 0) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					for (LatLong latLong : obstacleList) {
						reporter.addObstacle(MapMatchingUtil.doMatching(graph.getVerticeValues(), latLong.latitude,
								latLong.longitude));
					}
					return null;
				}
			};

		}
	}

	@Override
	public void onProcessComplete() {
		try {
			List<Vertex> result = astarProcessor.get();
			polyline = new Polyline(MapviewUtils.createPaint(AndroidGraphicFactory.INSTANCE.createColor(Color.BLUE), 7,
					Style.STROKE), AndroidGraphicFactory.INSTANCE);
			List<LatLong> latLongs = polyline.getLatLongs();

			for (Vertex vertex : result) {
				latLongs.add(new LatLong(Double.parseDouble(vertex.lat), Double.parseDouble(vertex.lon)));
			}
			layersOverlay.add(polyline);
			layerManager.getLayers().add(polyline);
			layerManager.redrawLayers();
			
			String[] details = AStar2.printPath(result);
			for(int i = 1;i<result.size();i++){
				String temp[] = details[i].split(",");
				
				String roadName = GraphAdapter.getRoadName(temp[0]);
				
				System.out.println(roadName + " || "+ temp[1] + " m");
			}
			
			System.out.println("TOTAL : "+details[result.size()] + " km");
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			AppUtil.logUser(this, reporter.getReport());
			shortestPathRunning = false;
			reporter = null;
			astarProcessor = null;

		}
	}

	// FIXME move to reporter
	private int progress = 100;
	private Handler handler = new Handler();
	private Runnable progressRunner = new Runnable() {
		@Override
		public void run() {
			progress += 1;

			int p = (android.view.Window.PROGRESS_END - android.view.Window.PROGRESS_START) / 100 * progress;
			setSupportProgress(p);

			if (progress < 100) {
				handler.postDelayed(progressRunner, 300);
			}
		}
	};

	protected Marker addMarker(LatLong latLong) {
		
		TextView bubbleView = new TextView(this);
		MapviewUtils.setBackground(bubbleView, getResources().getDrawable(R.drawable.balloon_overlay_unfocused));
		bubbleView.setGravity(Gravity.CENTER);
		bubbleView.setMaxEms(20);
		bubbleView.setTextSize(15);
		bubbleView.setTextColor(android.graphics.Color.BLACK);
		bubbleView.setText(latLong.toString());
		Bitmap bitmap = MapviewUtils.viewToBitmap(this, bubbleView);
		Marker marker = new Marker(latLong, bitmap, 0, -bitmap.getHeight() / 2);
		MapViewPosition mapPosition = mapView.getModel().mapViewPosition;
		mapPosition.setCenter(latLong);
		layerManager.getLayers().add(marker);
		layersOverlay.add(marker);
		layerManager.redrawLayers();
		
		return marker;
	}

}
