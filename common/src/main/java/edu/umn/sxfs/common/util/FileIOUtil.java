package edu.umn.sxfs.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.umn.sxfs.common.fileio.FileMemoryObject;

/**
 * Util Methods for file IO
 * 
 * @author prashant
 * 
 */
public final class FileIOUtil {

	private FileIOUtil() {
		throw new IllegalStateException(
				"This is a Util Class. Cannot Instantiate");
	}
	
	public static FileMemoryObject readFile(String filename) throws IOException {
		File f = new File(filename);
		byte[] readFromFile = readFromFile(f);
		return  new FileMemoryObject(filename, readFromFile);
	}
	
	public static void writeToDisk(FileMemoryObject fileObject) throws IOException {
		File f = new File(fileObject.getFilename());
		writeToFile(f, fileObject.getBytecontents());
	}
	
	private static void writeToFile(File f, byte[] bytes)
			throws IOException {

		// Create an output stream
		DataOutputStream stream = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(f)));

		// Delegate writing to the stream to a separate method
		writeToStream(stream, bytes);

		// Always be sure to flush & close the stream.
		stream.flush();
		stream.close();
	}

	private static byte[] readFromFile(File f) throws IOException {

		// Create an input stream
		DataInputStream stream = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));

		// Delegate reading from the stream to a separate method
		byte[] readFromStream = readFromStream(stream);
		
		// Always be sure to close the stream.
		stream.close();

		return readFromStream;
	}

	private static void writeToStream(DataOutputStream stream,
			byte[] bytes) throws IOException {
		stream.write(bytes);
		
	}
	
	private static byte[] readFromStream(DataInputStream stream)
			throws IOException {
		int available = stream.available();
		byte[] buffer = new byte[available];
		stream.readFully(buffer);
		stream.close();
		return buffer;
	}
}
