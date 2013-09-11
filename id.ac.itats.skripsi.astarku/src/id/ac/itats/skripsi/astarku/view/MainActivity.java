package id.ac.itats.skripsi.astarku.view;

import id.ac.itats.skripsi.astarku.AppUtil;
import id.ac.itats.skripsi.astarku.BasicMapViewer;
import id.ac.itats.skripsi.astarku.R;
import id.ac.itats.skripsi.astarku.processor.MapMatchingUtil;
import id.ac.itats.skripsi.astarku.processor.Reporter;
import id.ac.itats.skripsi.databuilder.PlaceContentProvider;
import id.ac.itats.skripsi.shortestpath.model.Vertex;
import id.ac.itats.skripsi.util.MapviewUtils;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.model.MapViewPosition;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.SearchView;

public class MainActivity extends BasicMapViewer implements ActionBar.OnNavigationListener, LoaderCallbacks<Cursor> {
	private final String TAG = MainActivity.class.getSimpleName();
	private SearchView searchView;
	private static final int RESULT_SETTINGS = 1;
	private SharedPreferences sharedPrefs;
	private String[] mLocations;
	private Marker marker = null;
	private Uri mUri;

	@Override
	protected MapView getMapView() {
		setContentView(R.layout.mapviewer);
		MapView mapView = (MapView) this.findViewById(R.id.mapView);

		mapView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent motionEvent) {
				if (gestureDetector.onTouchEvent(motionEvent)) {

					return true;
				}
				if ((motionEvent.getAction() != MotionEvent.ACTION_MOVE)) {
					gestureDetector.setIsLongpressEnabled(true);
				}
				return false;
			}
		});
		return mapView;
	}

	@Override
	protected void setContentView() {
	}

	@Override
	protected void addLayers(LayerManager layerManager, TileCache tileCache, MapViewPosition mapViewPosition) {
		super.addLayers(layerManager, tileCache, mapViewPosition);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initDecoration();
		super.onCreate(savedInstanceState);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		super.myLocationOverlay.disableMyLocation();

	}

	private void handleIntent(Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			doSearch(intent.getStringExtra(SearchManager.QUERY));
		} else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			mUri = intent.getData();
			if (marker != null) {
				layerManager.getLayers().remove(marker);
			}
			getSupportLoaderManager().restartLoader(0, null, this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void doSearch(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);

		// Invoking onCreateLoader() in non-ui thread
		getSupportLoaderManager().restartLoader(1, data, this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SETTINGS:
			boolean isLocationUpdate = sharedPrefs
					.getBoolean(getString(R.string.key_preferences_locationupdate), false);
			System.out.println(isLocationUpdate);

			if (isLocationUpdate == true) {
				super.myLocationOverlay.enableMyLocation(true);

				AppUtil.log(TAG, "" + super.myLocationOverlay.isMyLocationEnabled() + " - " + isLocationUpdate);
				super.myLocationOverlay.requestRedraw();

			}
			if (isLocationUpdate == false) {
				AppUtil.log(TAG, "" + super.myLocationOverlay.isMyLocationEnabled() + " - " + isLocationUpdate);
				super.myLocationOverlay.disableMyLocation();
				super.myLocationOverlay.requestRedraw();
			}

			boolean isLocationSnap = sharedPrefs.getBoolean(getString(R.string.key_preferences_locationsnap), false);
			super.myLocationOverlay.setSnapToLocationEnabled(isLocationSnap);

			break;

		default:
			break;
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		switch (itemPosition) {
		// home
		case 0:
			AppUtil.logUser(this, mLocations[itemPosition]);
			break;
		// search
		case 1:
			// onSearchRequested();

			break;
		// history
		case 2:
			AppUtil.logUser(this, mLocations[itemPosition]);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_mylocation:
			if (super.myLocationOverlay.isMyLocationEnabled()) {
				cleanLayerOverlay();
				obstacleList.clear();
				super.setStart(super.mapView.getModel().mapViewPosition.getMapPosition().latLong);
				super.myLocationOverlay.requestRedraw();
			} else {
				// TODO show dialog or go preference activity
				AppUtil.logUser(this, getString(R.string.astarku__userlocation_disable));
			}
			break;

		case R.id.action_reroute:
			if (polyline != null) {
				layerManager.getLayers().remove(polyline);
			}
			processAstar(start.latitude, start.longitude, end.latitude, end.longitude);

			break;

		case R.id.action_routing_details:
			AppUtil.logUser(this, "" + item.getTitle());
			startActivity(new Intent(MainActivity.this,FragmentLayoutSupport.class));
			break;
		case R.id.action_routing_algorithm:
			AppUtil.logUser(this, "" + item.getTitle());
			
			break;
		case R.id.action_panic:
			panic();
			break;

		case R.id.action_preferences:
			Intent i = new Intent(this, PreferencesActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;

		case R.id.action_help:
			AppUtil.logUser(this, "" + item.getTitle());

			break;

		case R.id.action_about:
			AppUtil.logUser(this, "" + item.getTitle());
			resetZoomLevel(14);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.main_contextmenu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.astarku__context_setasstart:
			AppUtil.log(TAG, "" + item.getTitle() + " " + super.touchLatLon);

			if (marker != null) {
				setStart(marker.getLatLong());
			} else {
				setStart(super.touchLatLon);
			}
			break;

		case R.id.astarku__context_setasend:
			AppUtil.log(TAG, "" + item.getTitle() + " " + super.touchLatLon);
			if (marker != null) {
				setEnd(marker.getLatLong());
			} else {
				setEnd(super.touchLatLon);
			}
			break;

		case R.id.astarku__context_setasobstacle:

			obstacleList.add(super.touchLatLon);
			AppUtil.log(TAG, "" + item.getTitle() + " " + super.touchLatLon);

			LatLong latlong = super.touchLatLon;
			if (marker != null) {
				latlong = marker.getLatLong();
			}
			Marker obstacle = MapviewUtils.createMarker(this, R.drawable.ic_obstacle, latlong);
			mapView.getLayerManager().getLayers().add(obstacle);
			mapView.getLayerManager().redrawLayers();
			layersOverlay.add(obstacle);

			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	protected void initDecoration() {
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		requestWindowFeature(Window.FEATURE_PROGRESS);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
		actionBar.setSplitBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
		setSupportProgressBarVisibility(false);
		actionBar.setDisplayShowTitleEnabled(false);
		// actionBar.setDisplayShowHomeEnabled(true);

		mLocations = getResources().getStringArray(R.array.locations);
		Context context = actionBar.getThemedContext();
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations,
				R.layout.sherlock_spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(list, this);

		actionBar.setCustomView(R.layout.actionbar_search);
		View actionBarCustomView = actionBar.getCustomView();
		searchView = ((SearchView) actionBarCustomView.findViewById(R.id.search_view));
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnSearchClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			}
		});

		searchView.setOnCloseListener(new SearchView.OnCloseListener() {

			@Override
			public boolean onClose() {
				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
				return false;
			}
		});

	}

	private void panic() {
		if (super.reporter == null) {
			super.reporter = new Reporter();
		}
		Location myLocation = myLocationOverlay.getLastLocation();
		Vertex obstacle = MapMatchingUtil.doMatching(super.graph.getVerticeValues(), myLocation.getLatitude(),
				myLocation.getLongitude());
		super.reporter.addObstacle(obstacle);
		super.processAstar(start.latitude, start.longitude, end.latitude, end.longitude);

		AppUtil.log(TAG, "reroute");
		AppUtil.log(TAG, "latlon : " + myLocation.getLatitude() + ", " + myLocation.getLongitude());
		AppUtil.log(TAG, "obstacle : " + reporter.getObstacleList());
		AppUtil.log(TAG, "start/end :" + start + ", " + end);
	}

	private void resetZoomLevel(int zoomLevel) {
		MapViewPosition mapPosition = super.mapView.getModel().mapViewPosition;
		mapPosition.setZoomLevel((byte) zoomLevel);
	}

	@Override
	public void onBackPressed() {

		if (searchView.getVisibility() == View.VISIBLE) {
			searchView.setIconified(true);
			return;
		}
	}

	// XXX searchresult
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle data) {
		CursorLoader cursorLoader = null;
		if (arg0 == 0) {
			cursorLoader = new CursorLoader(getBaseContext(), mUri, null, null, null, null);
		} else if (arg0 == 1) {
			cursorLoader = new CursorLoader(getBaseContext(), PlaceContentProvider.CONTENT_URI, null, null,
					new String[] { data.getString("query") }, null);
		}
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		if (c.moveToFirst()) {

			System.out.println("LatLon: " + c.getString(c.getColumnIndex(c.getColumnName(3))));
			String[] latlon = c.getString(c.getColumnIndex(c.getColumnName(3))).split(",");
			marker = addMarker(new LatLong(Double.parseDouble(latlon[0]), Double.parseDouble(latlon[1])));

		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

}