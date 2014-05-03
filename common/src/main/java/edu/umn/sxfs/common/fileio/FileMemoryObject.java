package edu.umn.sxfs.common.fileio;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The file object to store the contents of the file in memory form.
 * @author prashant
 *
 */
public final class FileMemoryObject implements Serializable{

	private static final long serialVersionUID = -8350413205570550488L;

	private String filename;
	private Map<String, byte[]> bytecontents = new HashMap<String, byte[]>();
	
	public FileMemoryObject(String filename, Map<String, byte[]> bytecontents) {
		this.filename = filename;
		this.bytecontents = bytecontents;
	}

	public String getFilename() {
		return filename;
	}

	public Map<String, byte[]> getBytecontents() {
		return bytecontents;
	}
}
