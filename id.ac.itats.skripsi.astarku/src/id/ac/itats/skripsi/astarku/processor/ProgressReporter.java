package id.ac.itats.skripsi.astarku.processor;

public interface ProgressReporter {
	void finish(boolean isFinish);
	
	void process(int process);
	
	void report(String report);
 }
