package id.ac.itats.skripsi.astarku;

import id.ac.itats.skripsi.databuilder.GraphAdapter;
import id.ac.itats.skripsi.util.StopWatch;
import android.util.Log;

public class GraphRunner {
	private final String TAG = GraphRunner.class.getSimpleName();
	private static GraphRunner instance;
	private StopWatch stopwatch;
	private float time;
	private Runnable runnable;

	private GraphRunner() {
		stopwatch = new StopWatch();
		runnable = new Runnable() {

			@Override
			public void run() {
				stopwatch.start();

				try {

					GraphAdapter.buildGraph();

					time = stopwatch.stop().getSeconds();
					Log.i(TAG, "" + GraphAdapter.getGraph().getSize());
					Log.i(TAG, "graph is ready!" + time);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

	}

	public void run() {
		Thread thread = new Thread(runnable, TAG);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public static synchronized GraphRunner getInstance() {
		if (instance == null) {
			instance = new GraphRunner();
		}
		return instance;
	}
}
