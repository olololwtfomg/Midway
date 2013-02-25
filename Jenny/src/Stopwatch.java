
public class Stopwatch {
	private long startTime = 0;
	private long before = 0;
	boolean paused = false;
	
	public Stopwatch() {
	}
	public Stopwatch(boolean start) {
		if (start) start();
	}
	
	public void start() {
		if (paused) { startTime = System.nanoTime() - before; paused = false; }
		else startTime = System.nanoTime();
	}
	public void pause() {
		if (!paused) {
			before += System.nanoTime() - startTime;
			paused = true;
		} else this.start();
	}
	public void reset() {
		startTime = 0;
		before = 0;
		paused = false;
	}
	public long actualTime() {
		return (System.nanoTime() - startTime + before);
	}
}
