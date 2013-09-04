package id.ac.itats.skripsi.astarku;

import id.ac.itats.skripsi.databuilder.GraphAdapter;
import id.ac.itats.skripsi.util.StopWatch;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class GraphService extends Service {
	private final String TAG =GraphService.class.getSimpleName();
	private final IBinder graphBinder = new GraphBinder();
	private StopWatch stopwatch = new StopWatch();
	private float time;
	private CharSequence notification;
	
	@Override
	public IBinder onBind(Intent intent) {
		return graphBinder;
	}

	@Override
	public void onCreate() {
		notification = getString(R.string.astar__graphservice_started);
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				stopwatch.start();

				try {
				
					GraphAdapter.buildGraph();
					
					time = stopwatch.stop().getSeconds();
					Log.i(TAG , ""+GraphAdapter.getGraph().getSize());
					Log.i(TAG , "graph is ready!" + time);
				
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		Thread thread = new Thread(runnable, TAG);
		thread.setPriority(Thread.MIN_PRIORITY);
				
		thread.start();
		
		
		super.onCreate();
	}
	
	public class GraphBinder extends Binder {
		GraphService getService(){
			return GraphService.this;
		}
	}

	public CharSequence getNotification() {
		notification = GraphAdapter.getGraph()!=null ? getString(R.string.astar__graphservice_finish)+"on "+getTime() : getString(R.string.astar__graphservice_running);
		return notification;
	}

	public float getTime() {
		return time;
	}
	
	
	
	

}
