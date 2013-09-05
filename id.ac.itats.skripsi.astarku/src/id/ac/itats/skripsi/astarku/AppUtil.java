package id.ac.itats.skripsi.astarku;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class AppUtil {
	public static void logUser(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void log(String TAG, String str) {
		Log.i(TAG, str);
	}
}
