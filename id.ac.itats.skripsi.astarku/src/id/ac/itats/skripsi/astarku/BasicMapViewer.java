package id.ac.itats.skripsi.astarku;

import id.ac.itats.skripsi.astarku.processor.AstarProcessor;
import id.ac.itats.skripsi.astarku.processor.ProcessListener;
import id.ac.itats.skripsi.astarku.processor.Reporter;
import id.ac.itats.skripsi.databuilder.GraphAdapter;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class BasicMapViewer extends SherlockActivity implements ProcessListener{
	protected final String TAG = BasicMapViewer.class.getSimpleName();
	
	
	// Mapview
	protected String mapFile = "surabaya_new.map";

	protected MapView mapView;
	protected PreferencesFacade preferencesFacade;
	protected TileCache tileCache;
	protected MyLocationOverlay myLocationOverlay;
	protected GestureDetector gestureDetector;
	protected LayerManager layerManager;
	protected volatile boolean shortestPathRunning = false;
	protected LatLong start, end;
	protected List<Layer> layersOverlay = new ArrayList<Layer>();

	
	// Process
	protected Reporter reporter;
	protected AstarProcessor astarProcessor;
	
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

			double pixelX = MercatorProjection.longitudeToPixelX(geoPoint.longitude, mapPosition.getZoomLevel());
			double pixelY = MercatorProjection.latitudeToPixelY(geoPoint.latitude, mapPosition.getZoomLevel());

			pixelX -= mapView.getWidth() >> 1;
			pixelY -= mapView.getHeight() >> 1;

			LatLong tmpPoint = new LatLong(MercatorProjection.pixelYToLatitude(pixelY + y, mapPosition.getZoomLevel()),
					MercatorProjection.pixelXToLongitude(pixelX + x, mapPosition.getZoomLevel()));
			if (start != null && end == null) {
				setEnd(tmpPoint);

				processAstar(start.latitude, start.longitude, end.latitude, end.longitude);
			} else {
				setStart(tmpPoint);
			}

			return true;
		}
	};

	protected void setStart(LatLong start) {
		this.start = start;
		end = null;

		for (Layer layer : layersOverlay) {
			layerManager.getLayers().remove(layer);
		}

		Marker marker = MapviewUtils.createMarker(BasicMapViewer.this, R.drawable.ic_marker_start, start);
		layersOverlay.add(marker);

		if (marker != null) {
			layerManager.getLayers().add(marker);
			layerManager.redrawLayers();
		}
	}

	protected void setEnd(LatLong end) {
		this.end = end;
		shortestPathRunning = true;

		Marker marker = MapviewUtils.createMarker(BasicMapViewer.this, R.drawable.ic_marker_end, end);
		layersOverlay.add(marker);
		if (marker != null) {
			layerManager.getLayers().add(marker);
			layerManager.redrawLayers();
		}

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

	protected void addMarker(MotionEvent motionEvent) {
		Marker marker = null;
		MapViewPosition mapPosition = mapView.getModel().mapViewPosition;
		LatLong geoPoint = mapPosition.getMapPosition().latLong;

		float x = motionEvent.getX();
		float y = motionEvent.getY();

		double pixelX = MercatorProjection.longitudeToPixelX(geoPoint.longitude, mapPosition.getZoomLevel());
		double pixelY = MercatorProjection.latitudeToPixelY(geoPoint.latitude, mapPosition.getZoomLevel());

		pixelX -= mapView.getWidth() >> 1;
		pixelY -= mapView.getHeight() >> 1;

		LatLong latLong = new LatLong(MercatorProjection.pixelYToLatitude(pixelY + y, mapPosition.getZoomLevel()),
				MercatorProjection.pixelXToLongitude(pixelX + x, mapPosition.getZoomLevel()));
		marker = MapviewUtils.createMarker(this, R.drawable.marker_red, latLong);
		this.mapView.getLayerManager().getLayers().add(marker);
		this.mapView.getLayerManager().redrawLayers();
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
	}

	protected void setContentView() {
		setContentView(this.mapView);
	}

	// TODO ROUTING		
	protected void processAstar(double fromLat, double fromLon, double toLat, double toLon){
		reporter = new Reporter();
		astarProcessor = new AstarProcessor(this, reporter);
		setSupportProgressBarVisibility(true);
		if (progress == 100) {
            progress = 0;
            progressRunner.run();
        }
		astarProcessor.execute(fromLat, fromLon, toLat, toLon);
	}
	
	@Override
	public void onProcessComplete() {
		try {
			List<Vertex> result = astarProcessor.get();
			Polyline polyline = new Polyline(MapviewUtils.createPaint(
					AndroidGraphicFactory.INSTANCE.createColor(Color.BLUE), 7, Style.STROKE),
					AndroidGraphicFactory.INSTANCE);
			List<LatLong> latLongs = polyline.getLatLongs();

			for (Vertex vertex : result) {
				latLongs.add(new LatLong(Double.parseDouble(vertex.lat), Double.parseDouble(vertex.lon)));
			}

			layerManager.getLayers().add(polyline);
			layerManager.redrawLayers();

			layersOverlay.add(polyline);
			
		}catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}finally{
			logUser(reporter.getReport());
		}
	}
	
	private int progress = 100;
	private Handler handler = new Handler();
	private Runnable progressRunner = new Runnable() {
        @Override
        public void run() {
            progress += 1;

            int p = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * progress;
            setSupportProgress(p);

            if (reporter.isFinish() == false) {
                handler.postDelayed(progressRunner, 300);
            }
        }
    };
    

    //UTIL
	protected void logUser(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}

	protected void log(String str) {
		Log.i(TAG, str);
	}


}
