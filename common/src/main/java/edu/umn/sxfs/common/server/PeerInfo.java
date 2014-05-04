package edu.umn.sxfs.common.server;

import java.io.Serializable;

import edu.umn.sxfs.common.exception.IllegalIPException;
import edu.umn.sxfs.common.validator.ContentValidator;


/**
 * Class for storing the attributes of server
 * @author prashant
 *
 */
public final class PeerInfo implements Serializable{
	private static final long serialVersionUID = -5998701872155160560L;
	private String ip;
	private int port;
	
	/**
	 * This will constructor will throw {@link IllegalArgumentException} if the ip is not valid
	 * @param ip
	 * @param port
	 * @throws IllegalArgumentException
	 */
	public PeerInfo(String ip, int port) throws IllegalIPException{
		if(!ContentValidator.isValidIp(ip)) {
			throw new IllegalIPException("Invalid Ip" + ip);
		}
		this.ip = ip;
		this.port = port;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return ip + ":" + port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeerInfo other = (PeerInfo) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
}
