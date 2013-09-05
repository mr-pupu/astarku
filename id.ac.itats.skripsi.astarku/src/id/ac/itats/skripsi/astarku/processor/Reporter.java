package id.ac.itats.skripsi.astarku.processor;

import id.ac.itats.skripsi.shortestpath.model.Vertex;

import java.util.ArrayList;
import java.util.List;

public class Reporter implements ProgressReporter {
	private String report;
	private int process;
	private boolean isFinish;
	private List<Vertex> obstacleList;

	@Override
	public void process(int process) {
		this.process = process;
	}

	@Override
	public void report(String report) {
		this.report = report;
	}

	@Override
	public void finish(boolean isFinish) {
		this.isFinish = isFinish;

	}

	public String getReport() {
		return report;
	}

	public int getProcess() {
		return process;
	}

	public boolean isFinish() {
		return isFinish;
	}

	@Override
	public List<Vertex> getObstacleList() {
		return obstacleList;
	}

	public void addObstacle(Vertex obstacle) {
		if(this.obstacleList==null){
			this.obstacleList = new ArrayList<Vertex>();
		}		
		this.obstacleList.add(obstacle);
	}

}
