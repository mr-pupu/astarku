package id.ac.itats.skripsi.astarku;

import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.model.MapViewPosition;

import android.view.MotionEvent;
import android.view.View;

public class BasicMapViewerXml extends BasicMapViewer {	
		
	@Override
	protected MapView getMapView() {
		setContentView(R.layout.mapviewer);
		MapView mapView = (MapView) this.findViewById(R.id.mapView);
		
		mapView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)){
					
					return true;
				}
				return false;
			}
		});
		return  mapView;
	}

	@Override
	protected void setContentView() {
	}
	
	@Override
	protected void addLayers(LayerManager layerManager, TileCache tileCache, MapViewPosition mapViewPosition) {
		super.addLayers(layerManager, tileCache, mapViewPosition);

	}

	
}