package id.ac.itats.skripsi.astarku.view;

import id.ac.itats.skripsi.astarku.BasicMapViewer;
import id.ac.itats.skripsi.astarku.R;

import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.model.MapViewPosition;

import com.actionbarsherlock.view.MenuItem;

import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends BasicMapViewer {

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
		case R.id.action_settings:
			logUser("" + item.getTitle());
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