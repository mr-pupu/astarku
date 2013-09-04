package id.ac.itats.skripsi.routingengine.limasatu;

import id.ac.itats.skripsi.databuilder.GraphAdapter;
import id.ac.itats.skripsi.shortestpath.engine.AStar2;
import id.ac.itats.skripsi.shortestpath.model.Graph;
import id.ac.itats.skripsi.shortestpath.model.Vertex;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TestActivity extends Activity {
	EditText editText;
	List<Vertex> obstaclesVertex;
	Button btnAdd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		editText = (EditText) findViewById(R.id.editText1);
		btnAdd = (Button) findViewById(R.id.button2);
		
		Intent in = new Intent(this, GraphService.class);
		startService(in);
		
	}
	
	public void addObstacle(View view){
		long id = Long.parseLong(editText.getText().toString());
		Vertex obs = GraphAdapter.getGraph().getVertex(id);
		if(obs!=null){
			System.out.println(obs.id);
			if(obstaclesVertex == null){
				obstaclesVertex = new ArrayList<Vertex>();
			}
			if(!obstaclesVertex.contains(obs)){
				obstaclesVertex.add(obs);
			}
			
			System.out.println(obstaclesVertex);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}
	
	public void runShortestpath(View view){
		Graph graph = GraphAdapter.getGraph();
		new Shortestpath().execute(graph.getVertex(1721121228), graph.getVertex(1722835557));	
		
	}
	
	
	private class Shortestpath extends AsyncTask<Vertex, Void, List<Vertex>>{
		Graph graph = GraphAdapter.getGraph();
		@Override
		protected List<Vertex> doInBackground(Vertex... params) {
			AStar2 aStar2 = new AStar2(graph);
			
			if(obstaclesVertex==null){
				obstaclesVertex=new ArrayList<Vertex>();
			}
			
			aStar2.setObstaclesVertex(obstaclesVertex);
			List<Vertex> path = aStar2.computePaths(params[0], params[1]);
			return path;
		}
		
		@Override
		protected void onPostExecute(List<Vertex> result) {
			System.out.println("path : "+result);
			obstaclesVertex=null;
			super.onPostExecute(result);
		}
		
	}

}
