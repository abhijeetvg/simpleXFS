package edu.umn.sxfs.common.fileio;

import java.io.Serializable;

/**
 * The file object to store the contents of the file in memory form.
 * @author prashant
 *
 */
public final class FileMemoryObject implements Serializable{

	private static final long serialVersionUID = -8350413205570550488L;

	private String filename;
	private byte[] bytecontents = null;
	
	public FileMemoryObject(String filename, byte[] bytecontents) {
		this.filename = filename;
		int length = bytecontents.length;
		this.bytecontents = new byte[length];
		for(int i = 0; i < length; ++i) {
			this.bytecontents[i] = bytecontents[i];
		}
	}

	public String getFilename() {
		return filename;
	}

	public byte[] getBytecontents() {
		return bytecontents;
	}
}
