package edu.umn.sxfs.peer.store;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains the list of files present in the directory.
 * @author prashant
 *
 */
public final class FileStore {
	private final String SRC_DIRECTORY = "~/5105/xfs/";
	private final Set<String> filenames = new HashSet<String>();
	private static FileStore instance = null;
	
	private FileStore() {
		if(instance != null) {
			throw new IllegalStateException("Cannot instantiate FileStore twice.");
		}
	}
	
	public static FileStore getInstance() {
		if(instance == null) {
			synchronized (FileStore.class) {
				if(instance == null) {
					instance = new FileStore();
				}
			}
		}
		return instance;
	}
	
	public synchronized void initialize() {
		File srcDirectory = new File(SRC_DIRECTORY);
		for (File file : srcDirectory.listFiles()) {
			filenames.add(file.getName());
		}
	}
	
	public synchronized boolean containsFile(String filename) {
		return filenames.contains(filename);
	}
	
	public synchronized Set<String> getFilenames() {
		return filenames;
	}
}
