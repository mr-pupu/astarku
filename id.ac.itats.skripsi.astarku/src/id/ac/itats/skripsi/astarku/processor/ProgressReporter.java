package id.ac.itats.skripsi.astarku.processor;

import id.ac.itats.skripsi.shortestpath.model.Vertex;

import java.util.List;

public interface ProgressReporter {
	void finish(boolean isFinish);
	
	void process(int process);
	
	void report(String report);
	
	List<Vertex> getObstacleList();
 }
