package id.ac.itats.skripsi.astarku.view;

import id.ac.itats.skripsi.astarku.BasicMapViewer;
import id.ac.itats.skripsi.astarku.R;

import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.model.MapViewPosition;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends BasicMapViewer {
	private static final int RESULT_SETTINGS = 1;

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		switch (requestCode) {
		case RESULT_SETTINGS:
			boolean isLocationUpdate = sharedPrefs.getBoolean(getString(R.string.key_preferences_locationupdate), false);
			System.out.println(isLocationUpdate);
			
			if (isLocationUpdate == true) {
				super.myLocationOverlay.enableMyLocation(true);
				setStart(super.mapView.getModel().mapViewPosition.getMapPosition().latLong);
				super.myLocationOverlay.requestRedraw();

			} else {
				super.myLocationOverlay.disableMyLocation();
				super.myLocationOverlay.requestRedraw();
			}
			log("" + super.myLocationOverlay.isMyLocationEnabled() + " - " + isLocationUpdate);
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
		return super.onNavigationItemSelected(itemPosition, itemId);
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

}