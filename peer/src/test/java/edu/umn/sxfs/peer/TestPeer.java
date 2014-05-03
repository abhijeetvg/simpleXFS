package edu.umn.sxfs.peer;

import java.io.IOException;
import java.util.Set;

import edu.umn.sxfs.common.fileio.FileMemoryObject;
import edu.umn.sxfs.common.util.FileIOUtil;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.common.util.MD5CheckSumUtil;
import edu.umn.sxfs.peer.file.FileStore;

public class TestPeer {
	private static final String CLASS_NAME = TestPeer.class.getSimpleName(); 

	public static void main(String[] args) throws IOException {
		final String method = CLASS_NAME + ".main()"; 
		boolean start = Peer.start(args);
		if(!start) {
			LogUtil.log(method, "Peer not initialized properly");
			return;
		}
		Set<String> filenames = FileStore.getInstance().getFilenames();
		if(filenames == null || filenames.isEmpty()) {
			LogUtil.log(method, "No files in directory");
			return;
		}
		for (String filename : filenames) {
			String fullPathFileName = FileStore.getInstance().getFileStoreDirectory() + filename;
			LogUtil.log(method, "Reading : " + fullPathFileName);
			FileMemoryObject readFile = null;
			try {
				readFile = FileIOUtil.readFile(fullPathFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			LogUtil.log(method, "DONE Reading : " +  filename);
			LogUtil.log(method, "calculating MD5 checksum : " +  filename);
			byte[] createChecksumBeforeWriting = MD5CheckSumUtil.createChecksum(fullPathFileName);
			LogUtil.log(method, "DONE calculating MD5 checksum : " +  filename);
			
			FileMemoryObject writeFileOnject = new FileMemoryObject(filename, readFile.getBytecontents());
			
			LogUtil.log(method, "Writing : " +  filename);
			try {
				FileIOUtil.writeToDisk(writeFileOnject);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			LogUtil.log(method, "DONE Writing : " +  filename);
			LogUtil.log(method, "calculating MD5 checksum : " +  filename);
			byte[] createChecksumAfterWriting = MD5CheckSumUtil.createChecksum(filename);
			LogUtil.log(method, "DONE calculating MD5 checksum : " +  filename);
			
			if(MD5CheckSumUtil.isEqualCheckSum(createChecksumBeforeWriting, createChecksumAfterWriting)) {
				LogUtil.log(method, "CheckSums equal");
			}else{
				LogUtil.log(method, "CheckSums NOT equal");
			}
		}
	}

}
