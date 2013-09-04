package id.ac.itats.skripsi.astarku.view;

import id.ac.itats.skripsi.astarku.BasicMapViewer;
import id.ac.itats.skripsi.astarku.R;

import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.model.MapViewPosition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class MainActivity extends BasicMapViewer implements ActionBar.OnNavigationListener {
	private static final int RESULT_SETTINGS = 1;
	private SharedPreferences sharedPrefs;
	private String[] mLocations;
	
	@Override
	protected MapView getMapView() {
		setContentView(R.layout.mapviewer);
		MapView mapView = (MapView) this.findViewById(R.id.mapView);

		mapView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {

					return true;
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		
		switch (requestCode) {
		case RESULT_SETTINGS:
			boolean isLocationUpdate = sharedPrefs.getBoolean(getString(R.string.key_preferences_locationupdate), false);
			System.out.println(isLocationUpdate);
			
			if (isLocationUpdate == true) {
				super.myLocationOverlay.enableMyLocation(true);
				
				log("" + super.myLocationOverlay.isMyLocationEnabled() + " - " + isLocationUpdate);
				super.myLocationOverlay.requestRedraw();

			} 
			if (isLocationUpdate == false) {
				log("" + super.myLocationOverlay.isMyLocationEnabled() + " - " + isLocationUpdate);
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
			logUser(mLocations[itemPosition]);
			break;
		// search
		case 1:
			logUser(mLocations[itemPosition]);
			break;
		// history
		case 2:
			logUser(mLocations[itemPosition]);
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
				setStart(super.mapView.getModel().mapViewPosition.getMapPosition().latLong);
				super.myLocationOverlay.requestRedraw();
			} else {
				// TODO show dialog or go preference activity
				logUser("User location currently disable !");
			}

			break;

		case R.id.action_preferences:
			Intent i = new Intent(this, PreferencesActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;

		case R.id.action_help:
			logUser("" + item.getTitle());

			break;

		case R.id.action_about:
			logUser("" + item.getTitle());
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	

	protected void initDecoration() {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		requestWindowFeature(Window.FEATURE_PROGRESS);

		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
		getSupportActionBar().setSplitBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
		setSupportProgressBarVisibility(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		mLocations = getResources().getStringArray(R.array.locations);
		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations,
				R.layout.sherlock_spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
	}

}