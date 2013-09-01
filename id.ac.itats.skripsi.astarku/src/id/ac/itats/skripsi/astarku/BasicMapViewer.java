package id.ac.itats.skripsi.astarku;

import id.ac.itats.skripsi.databuilder.GraphAdapter;
import id.ac.itats.skripsi.shortestpath.engine.AStar2;
import id.ac.itats.skripsi.shortestpath.model.Graph;
import id.ac.itats.skripsi.shortestpath.model.Vertex;
import id.ac.itats.skripsi.util.MapMatchingUtil;
import id.ac.itats.skripsi.util.MapviewUtils;
import id.ac.itats.skripsi.util.Reporter;

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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;

public class BasicMapViewer extends SherlockActivity implements ActionBar.OnNavigationListener {
	protected final String TAG = BasicMapViewer.class.getSimpleName();

	// Activity
	protected String[] mLocations;
	// Mapview
	protected String mapFile = "surabaya_new.map";

	protected MapView mapView;
	protected PreferencesFacade preferencesFacade;
	protected TileCache tileCache;

	protected GestureDetector gestureDetector;
	protected LayerManager layerManager;
	protected volatile boolean shortestPathRunning = false;
	protected LatLong start, end;
	protected List<Layer> layersOverlay = new ArrayList<Layer>();

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
				end = tmpPoint;
				shortestPathRunning = true;

				Marker marker = MapviewUtils.createMarker(BasicMapViewer.this, R.drawable.marker_red, tmpPoint);
				layersOverlay.add(marker);
				if (marker != null) {
					layerManager.getLayers().add(marker);
					layerManager.redrawLayers();
				}

				calcPath(start.latitude, start.longitude, end.latitude, end.longitude);

			} else {
				start = tmpPoint;
				end = null;

				for (Layer layer : layersOverlay) {
					layerManager.getLayers().remove(layer);
				}

				Marker marker = MapviewUtils.createMarker(BasicMapViewer.this, R.drawable.marker_green, start);
				layersOverlay.add(marker);

				if (marker != null) {
					layerManager.getLayers().add(marker);
					layerManager.redrawLayers();
				}
			}

			return true;
		}
	};

	protected void addLayers(LayerManager layerManager, TileCache tileCache, MapViewPosition mapViewPosition) {
		layerManager.getLayers().add(MapviewUtils.createTileRendererLayer(tileCache, mapViewPosition, getMapFile()));
	}

	protected TileCache createTileCache() {
		return MapviewUtils.createExternalStorageTileCache(this, getPersistableId());
	}

	protected MapPosition getInitialPosition() {
		return new MapPosition(new LatLong(-7.2517722, 112.6822205), (byte) 14);
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

		initDecoration();

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
	}

	protected void setContentView() {

		setContentView(this.mapView);
	}

	protected void initDecoration() {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		requestWindowFeature(Window.FEATURE_PROGRESS);

		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
		setSupportProgressBarVisibility(false);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		mLocations = getResources().getStringArray(R.array.locations);
		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations,
				R.layout.sherlock_spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
	}

	protected void logUser(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}

	protected void log(String str) {
		Log.i(TAG, str);
	}

	// XXX ROUTING
	protected void calcPath(final double fromLat, final double fromLon, final double toLat, final double toLon) {
		new AsyncTask<Void, Integer, List<Vertex>>() {			
			int mProgress = 100;
			Handler mHandler = new Handler();
			Runnable mProgressRunner = new Runnable() {
				@Override
				public void run() {
					mProgress +=1;

					int progress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * mProgress;
					setSupportProgress(progress);

					if (reporter.isFinish() == false) {
						mHandler.postDelayed(mProgressRunner, 300);
					}
				}
			};
			
			Graph graph = GraphAdapter.getGraph();
			Reporter reporter = new Reporter();

			@Override
			protected void onPreExecute() {

				setSupportProgressBarVisibility(true);				

			}

			@Override
			protected List<Vertex> doInBackground(Void... params) {
				
				if (mProgress == 100) {
                    mProgress = 0;
                    mProgressRunner.run();
                }
				
				Vertex source = MapMatchingUtil.doMatching(graph.getVerticeValues(), fromLat, fromLon);

				System.out.println(source.id);

				Vertex target = MapMatchingUtil.doMatching(graph.getVerticeValues(), toLat, toLon);

				System.out.println(target.id);
				
				
				AStar2 aStar2 = new AStar2(graph, reporter);				
				
				List<Vertex> path = aStar2.computePaths(source, target);

//				while (mProgress < 100) {
//					mProgress += 1;
//
//					SystemClock.sleep(300);
//					publishProgress(mProgress);
//
//				}
				return path;
			}

			@Override
			protected void onPostExecute(List<Vertex> result) {

				if (result != null) {
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
					logUser(reporter.getReport());
				} else {
					logUser(reporter.getReport());
					for (Layer layer : layersOverlay) {
						layerManager.getLayers().remove(layer);
					}

				}

				shortestPathRunning = false;
				setSupportProgressBarVisibility(false);

			}
		}.execute();
	}


	    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu("Option");
		sub.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark);
		sub.add(0, R.style.AppTheme, 0, "Setting");
		sub.add(0, R.style.AppTheme, 0, "Help");
		sub.add(0, R.style.AppTheme, 0, "About");
		sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		System.out.println("Selected: " + mLocations[itemPosition]);
		return true;
	}

}
