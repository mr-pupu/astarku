package id.ac.itats.skripsi.astarku;

import id.ac.itats.skripsi.databuilder.GraphAdapter;
import id.ac.itats.skripsi.util.MapviewUtils;
import id.ac.itats.skripsi.util.StopWatch;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GraphService extends Service {
	private final String TAG =GraphService.class.getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				StopWatch sw = new StopWatch().start();

				try {
				
					GraphAdapter.buildGraph();
					
					Log.i(TAG , ""+GraphAdapter.getGraph().getSize());
					Log.i(TAG , "graph is ready!" + sw.stop().getSeconds());
				
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		Thread thread = new Thread(runnable, "graph builder");
		thread.setPriority(Thread.MIN_PRIORITY);
				
		thread.start();
		
		
		super.onCreate();
	}
	
	

}
