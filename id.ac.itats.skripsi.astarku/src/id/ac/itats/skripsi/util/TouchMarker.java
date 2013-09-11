package id.ac.itats.skripsi.util;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.overlay.Marker;

import android.util.Log;

public class TouchMarker extends Marker {
	
	
	private final String TAG = TouchMarker.class.getSimpleName();

	public TouchMarker(LatLong latLong, Bitmap bitmap, int horizontalOffset, int verticalOffset) {
		super(latLong, bitmap, horizontalOffset, verticalOffset);
		// TODO Auto-generated constructor stub
	}
	
	public void onTap(){
		 Log.d(TAG , "The Marker was touched with onTap: " + this.getLatLong().toString());
	}

}
