package id.ac.itats.skripsi.util;

import id.ac.itats.skripsi.astarku.RoutingEngine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

public final class MapviewUtils {
	private static String TAG = "MapviewUtils";
	
	@TargetApi(11)
	public static void enableHome(Activity a) {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			a.getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	public static void setBackground(View view, Drawable background) {
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			view.setBackgroundDrawable(background);
		} else {
			view.setBackgroundDrawable(background);
		}
	}

	public static TileCache createExternalStorageTileCache(Context c, String id) {
		TileCache firstLevelTileCache = new InMemoryTileCache(32);
		String cacheDirectoryName = c.getExternalCacheDir().getAbsolutePath() + File.separator + id;
		File cacheDirectory = new File(cacheDirectoryName);
		if (!cacheDirectory.exists()) {
			cacheDirectory.mkdir();
		}
		TileCache secondLevelTileCache = new FileSystemTileCache(1024, cacheDirectory, AndroidGraphicFactory.INSTANCE);
		return new TwoLevelTileCache(firstLevelTileCache, secondLevelTileCache);
	}

	public static Marker createMarker(Context c, int resourceIdentifier, LatLong latLong) {
		Drawable drawable = c.getResources().getDrawable(resourceIdentifier);
		Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
		return new Marker(latLong, bitmap, 0, -bitmap.getHeight() / 2);
	}

	public static Paint createPaint(int color, int strokeWidth, Style style) {
		Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
		paint.setColor(color);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(style);
		paint.setDashPathEffect(new float[] { 25, 15 });
		
		return paint;
	}

	public static TileCache createTileCache(Context c, String id) {
		TileCache firstLevelTileCache = new InMemoryTileCache(32);
		File cacheDirectory = c.getDir(id, Context.MODE_PRIVATE);
		TileCache secondLevelTileCache = new FileSystemTileCache(1024, cacheDirectory, AndroidGraphicFactory.INSTANCE);
		return new TwoLevelTileCache(firstLevelTileCache, secondLevelTileCache);
	}

	public static Layer createTileRendererLayer(TileCache tileCache, MapViewPosition mapViewPosition, File mapFile) {
		if(!mapFile.exists()){
			copyMap();
		}
		
		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapViewPosition,
				AndroidGraphicFactory.INSTANCE);
		tileRendererLayer.setMapFile(mapFile);
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
		tileRendererLayer.setTextScale(1.5f);
		return tileRendererLayer;
	}

	public static Bitmap viewToBitmap(Context c, View view) {
		view.measure(MeasureSpec.getSize(view.getMeasuredWidth()), MeasureSpec.getSize(view.getMeasuredHeight()));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Drawable drawable = new BitmapDrawable(c.getResources(), android.graphics.Bitmap.createBitmap(view
				.getDrawingCache()));
		view.setDrawingCacheEnabled(false);
		return AndroidGraphicFactory.convertToBitmap(drawable);
	}

	private static void copyMap(){
		String name = "surabaya_new.map";
		String path = Environment.getExternalStorageDirectory()+ "/routingengine/maps/";		
		
		File dir = new File(path);
		File file = new File(path + name);
		
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		if(!file.exists()){			
			try {
				
				InputStream mInput = RoutingEngine.getAppContext().getAssets().open(name);
				String outFileName = path + name;
				OutputStream mOutput = new FileOutputStream(outFileName);
				byte[] mBuffer = new byte[1024];
				int mLength;
				while ((mLength = mInput.read(mBuffer)) > 0) {
					mOutput.write(mBuffer, 0, mLength);
				}
				mOutput.flush();
				mOutput.close();
				mInput.close();
				
				Log.i(TAG, "copy succes..");
			} catch(IOException e) {
				throw new Error(e);
			}
		}	
	}
	
	private MapviewUtils() {
		throw new IllegalStateException();
	}
}
