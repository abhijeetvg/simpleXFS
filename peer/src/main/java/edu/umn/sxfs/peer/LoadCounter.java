package edu.umn.sxfs.peer;

/**
 * Singleton to handle the load counts.
 * @author prashant
 *
 */
public final class LoadCounter {
	private static LoadCounter load;
	private int loadCounter = 0;
	
	private LoadCounter() {
		if(load != null) {
			throw new IllegalStateException("Singleton class. Cannot instantiate.");
		}
		loadCounter = 0;
	}
	
	public static LoadCounter getLoad() {
		if(load == null) {
			synchronized (LoadCounter.class) {
				if(load == null) {
					load = new LoadCounter();
				}
			}
		}
		return load;
	}
	
	public synchronized int value() {
		return loadCounter;
	}
	
	public synchronized void increaseLoad() {
		++loadCounter;
	}
	
	public synchronized void decreaseLoad() {
		--loadCounter;
	}
}
