package edu.umn.sxfs.peer.client.cli;

import edu.umn.sxfs.common.util.StringUtil;
import edu.umn.sxfs.peer.client.constants.CommandConstants;
import edu.umn.sxfs.peer.client.exceptions.IllegalCommandException;

/**
 * Factory that returns appropriate Command Handler to handle
 * the command sent by User.
 * 
 * @author Abhijeet
 *
 */
public class CommandFactory {

	private static final String FIND_PREFIX = "find";
	private static final String DOWNLOAD_AND_CHECK_CHECKSUM_PREFIX = "downloadandchecksum";
	private static final String DOWNLOAD_PREFIX = "download";
	private static final String CHECKSUM_PREFIX = "getchecksum";
    private static final String LOAD_PREFIX = "getload";
    private static final String ULOS_PREFIX = "updatelist";
		
	public static BaseCommand getCommand(String cmd) 
			throws IllegalCommandException {
		
		String prefix = StringUtil.getCmdPrefix(cmd).trim();

		if (prefix.equalsIgnoreCase(DOWNLOAD_PREFIX)) {
			return new DownloadCmd(cmd,false);
		} else if (prefix.equalsIgnoreCase(FIND_PREFIX)) {
			return new FindCmd(cmd);
		} else if (prefix.equalsIgnoreCase(CHECKSUM_PREFIX)) {
			return new ChecksumCmd(cmd);
		} else if (prefix.equalsIgnoreCase(LOAD_PREFIX)) {
			return new LoadCmd(cmd);
		} else if (prefix.equalsIgnoreCase(ULOS_PREFIX)) {
			// TODO this not updalelist  
            return new DownloadCmd(cmd,false);
        } else if (prefix.equalsIgnoreCase(DOWNLOAD_AND_CHECK_CHECKSUM_PREFIX)) {
        	return new DownloadCmd(cmd, true);
        }
		
		throw new IllegalCommandException(prefix + " " 
				+ CommandConstants.ERR_COMMAND_NOT_FOUND);
	}
}

