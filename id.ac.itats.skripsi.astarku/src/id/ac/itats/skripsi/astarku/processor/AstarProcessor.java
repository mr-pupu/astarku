package id.ac.itats.skripsi.astarku.processor;

import id.ac.itats.skripsi.databuilder.GraphAdapter;
import id.ac.itats.skripsi.shortestpath.engine.AStar2;
import id.ac.itats.skripsi.shortestpath.model.Graph;
import id.ac.itats.skripsi.shortestpath.model.Vertex;

import java.util.List;

import android.os.AsyncTask;

public class AstarProcessor extends AsyncTask<Double, Integer, List<Vertex>>{
	private ProcessListener processListener;
	private ProgressReporter progressReporter;
	private Graph graph;
	
	public AstarProcessor(ProcessListener processListener, ProgressReporter progressReporter) {
		this.processListener=processListener;
		this.progressReporter=progressReporter;
		
		graph = GraphAdapter.getGraph();
		
	}
	
	@Override
	protected List<Vertex> doInBackground(Double... params) {
		
		Vertex source = MapMatchingUtil.doMatching(graph.getVerticeValues(), params[0], params[1]);

		System.out.println(source.id);

		Vertex target = MapMatchingUtil.doMatching(graph.getVerticeValues(), params[2], params[3]);

		System.out.println(target.id);

		AStar2 aStar2 = new AStar2(graph, progressReporter);

		List<Vertex> path = aStar2.computePaths(source, target);

		return path;
		
	}
	
	@Override
	protected void onPostExecute(List<Vertex> result) {
		processListener.onProcessComplete();
		super.onPostExecute(result);
	}

}
