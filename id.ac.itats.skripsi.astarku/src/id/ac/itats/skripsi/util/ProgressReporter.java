package id.ac.itats.skripsi.util;

public interface ProgressReporter {
	void finish(boolean isFinish);
	
	void process(int process);
	
	void report(String report);
 }
