package edu.umn.sxfs.peer;

import java.io.*;
import java.util.Properties;

import edu.umn.sxfs.common.exception.WrongPropertiesFileFormatException;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.common.validator.ContentValidator;

/**
 * This file contains the server config which is initialized from the property
 * file.
 *
 * @author prashant
 *
 */
public class PeerConfig {
    private static final String CLASS_NAME = PeerConfig.class.getSimpleName();

    private PeerConfig() {
        throw new IllegalStateException("Config class cannot instantiate");
    }

    private static String peerAlgorithm;
    private static int loadThreshold;
    private static String trackingServerIp;
    private static int trackingServerPort;
    private static String fileStoreDirectory;
    private static String peerPeerLatencyFile;
    private static boolean isByzantineMode;

    public static void loadProperties(String filename) throws WrongPropertiesFileFormatException, FileNotFoundException {
        final String method = CLASS_NAME + ".loadProperties()";
        InputStream input = null;

        input = new FileInputStream(filename);

        Properties prop = new Properties();
        try {
            prop.load(input);
        } catch (IOException e) {
            LogUtil.log(method, "Cannot load the properties file. Exiting.");
            System.exit(1);
        }

        // Peer Algorithm
        String temp = prop.getProperty(PeerPropertiesConstants.PEER_ALGORITHM);
        if(temp == null || temp.isEmpty()) {
            LogUtil.log(method, "No Peer algorithm in property file. Setting it to default : " + PeerPropertiesConstants.DEFAULT_PEER_ALGORITHM);
            peerAlgorithm = PeerPropertiesConstants.DEFAULT_PEER_ALGORITHM;
        }
        peerAlgorithm = temp;

        // Load threshold
        temp = prop.getProperty(PeerPropertiesConstants.LOAD_THRESHOLD);
        if(temp == null || temp.isEmpty()) {
            LogUtil.log(method, "No Load threshold in property file. Setting it to default : " + PeerPropertiesConstants.DEFAULT_LOAD);
            loadThreshold = PeerPropertiesConstants.DEFAULT_LOAD;
        }else{
            loadThreshold = Integer.parseInt(temp);
        }

        // tracking server ip
        temp = prop.getProperty(PeerPropertiesConstants.TRACKING_SERVER_IP);
        if(temp == null || temp.isEmpty()) {
            throw new WrongPropertiesFileFormatException(PeerPropertiesConstants.TRACKING_SERVER_IP + " not present.");
        }else{
            if(!ContentValidator.isValidIp(temp)) {
                throw new WrongPropertiesFileFormatException("Illegal Ip: " + temp);
            }
            trackingServerIp = temp;
        }

        // tracking server port
        temp = prop.getProperty(PeerPropertiesConstants.TRACKING_SERVER_PORT);
        if(temp == null || temp.isEmpty()) {
            throw new WrongPropertiesFileFormatException(PeerPropertiesConstants.TRACKING_SERVER_PORT + " not present.");
        }else{
            if(!ContentValidator.isValidPort(temp)) {
                throw new WrongPropertiesFileFormatException("Illegal port: " + temp);
            }
            trackingServerPort = Integer.parseInt(temp);
        }

        // file store directory
        temp = prop.getProperty(PeerPropertiesConstants.FILE_STORE_DIRECTORY);
        if(temp == null || temp.isEmpty()) {
            throw new WrongPropertiesFileFormatException("fileStoreDir should be provided in properties file.");
        }else{
            File f = new File(temp);
            if (!f.exists()) {
                throw new WrongPropertiesFileFormatException(PeerPropertiesConstants.FILE_STORE_DIRECTORY + " not present.");
            }
            if (!f.isDirectory()) {
                throw new WrongPropertiesFileFormatException(PeerPropertiesConstants.FILE_STORE_DIRECTORY + " not a directory.");
            }
            fileStoreDirectory = temp;
        }

        // peer peer latency file
        temp = prop.getProperty(PeerPropertiesConstants.PEER_PEER_LATENCY_FILE);
        if(temp == null || temp.isEmpty()) {
            throw new WrongPropertiesFileFormatException(PeerPropertiesConstants.PEER_PEER_LATENCY_FILE + " not present.");
        }else{
            peerPeerLatencyFile = temp;
        }

        // byzantine mode
        temp = prop.getProperty(PeerPropertiesConstants.BYZANTINE_MODE);
        if(temp == null || temp.isEmpty()) {
            LogUtil.log(method, "No Byzantinemode in property file. Setting it to default : "
                    + PeerPropertiesConstants.DEFAULT_BYZANTINE_MODE);
            isByzantineMode = PeerPropertiesConstants.DEFAULT_BYZANTINE_MODE;
        }else{
            isByzantineMode = new Boolean(temp);
        }
    }

    public static String getPeerAlgorithm() {
        return peerAlgorithm;
    }

    public static int getLoadThreshold() {
        return loadThreshold;
    }

    public static String getTrackingServerIp() {
        return trackingServerIp;
    }

    public static int getTrackingServerPort() {
        return trackingServerPort;
    }

    public static String getFileStoreDirectory() {
        return fileStoreDirectory;
    }

    public static String getPeerPeerLatencyFile() {
        return peerPeerLatencyFile;
    }

    public static boolean isByzantineMode() {
        return isByzantineMode;
    }
}