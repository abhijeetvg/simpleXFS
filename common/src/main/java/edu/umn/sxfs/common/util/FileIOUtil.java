package edu.umn.sxfs.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
		Map<String, byte[]> readFromFile = readFromFile(f);
		return  new FileMemoryObject(filename, readFromFile);
	}
	
	public static void writeToDisk(FileMemoryObject fileObject) throws IOException {
		File f = new File(fileObject.getFilename());
		writeToFile(f, fileObject.getBytecontents());
	}
	
	private static void writeToFile(File f, Map<String, byte[]> map)
			throws IOException {

		// Create an output stream
		DataOutputStream stream = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(f)));

		// Delegate writing to the stream to a separate method
		writeToStream(stream, map);

		// Always be sure to flush & close the stream.
		stream.flush();
		stream.close();
	}

	private static Map<String, byte[]> readFromFile(File f) throws IOException {

		// Create an input stream
		DataInputStream stream = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));

		// Delegate reading from the stream to a separate method
		Map<String, byte[]> map = readFromStream(stream);

		// Always be sure to close the stream.
		stream.close();

		return map;
	}

	private static void writeToStream(DataOutputStream stream,
			Map<String, byte[]> map) throws IOException {

		// First, write the number of entries in the map.
		stream.writeInt(map.size());

		// Next, iterate through all the entries in the map
		for (Map.Entry<String, byte[]> entry : map.entrySet()) {

			// Write the name of this piece of data.
			stream.writeUTF(entry.getKey());

			// Write the data represented by this name, making sure to
			// prefix the data with an integer representing its length.
			byte[] data = entry.getValue();
			stream.writeInt(data.length);
			stream.write(data);
		}
	}

	private static Map<String, byte[]> readFromStream(DataInputStream stream)
			throws IOException {

		// Create the data structure to contain the data from my custom file
		Map<String, byte[]> map = new HashMap<String, byte[]>();

		// Read the number of entries in this file
		int entryCount = stream.readInt();

		// Iterate through all the entries in the file, and add them to the map
		for (int i = 0; i < entryCount; i++) {

			// Read the name of this entry
			String name = stream.readUTF();

			// Read the data associated with this name, remembering that the
			// data has an integer prefix representing the array length.
			int dataLength = stream.readInt();
			byte[] data = new byte[dataLength];
			stream.read(data, 0, dataLength);

			// Add this entry to the map
			map.put(name, data);
		}
		return map;
	}
}
