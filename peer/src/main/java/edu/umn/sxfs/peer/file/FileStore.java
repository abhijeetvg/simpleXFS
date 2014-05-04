package edu.umn.sxfs.peer.file;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.peer.Peer;

/**
 * Contains the list of files present in the directory.
 * @author prashant
 *
 */
public final class FileStore {
	private final String CLASS_NAME = FileStore.class.getSimpleName();
	private String fileStoreDirectory = "5105/xfs/" + Peer.getIp() + Peer.getPort();
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
	
	public synchronized void initialize(String srcDirectoryStr) {
		final String method = CLASS_NAME + ".initialize()";
		fileStoreDirectory = srcDirectoryStr;
		File srcDirectory = new File(fileStoreDirectory);
		
		File[] listFiles = srcDirectory.listFiles();
		if(listFiles == null) {
			LogUtil.log(method, "List of files is null in " + fileStoreDirectory); 
			return;
		}
		for (File file : listFiles) {
			filenames.add(file.getName());
		}
	}
	
	public synchronized boolean containsFile(String filename) {
		return filenames.contains(filename);
	}
	
	public synchronized Set<String> getFilenames() {
		return filenames;
	}
	
	public synchronized boolean addFilename(String filename) {
		return filenames.add(filename);
	}
	
	public synchronized String getFileStoreDirectory() {
		return fileStoreDirectory + "/";
	}
	
	public synchronized void printStore() {
		final String method = CLASS_NAME + ".printScore()";
		LogUtil.log(method, "Store: " + filenames.toString());
	}
}
