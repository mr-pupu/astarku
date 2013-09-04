package id.ac.itats.skripsi.astarku.processor;

public class Reporter implements ProgressReporter {
	private String report;
	private int process;
	private boolean isFinish;
	
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

	

	

}
