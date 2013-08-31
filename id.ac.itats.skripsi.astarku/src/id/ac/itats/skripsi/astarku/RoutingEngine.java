package id.ac.itats.skripsi.astarku;

import android.app.Application;
import android.content.Context;

//Global variabel binding
public class RoutingEngine  extends Application{
	public static final String TAG = "AstarKu";
    private static Context context;

    @Override
	public void onCreate(){
        super.onCreate();
        RoutingEngine.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return RoutingEngine.context;
    }

	
	@Override
	public String toString() {
		return "AstarKu instance";
	}
	
	
}
