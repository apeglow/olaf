package de.mobile.olaf.server.domain;

import java.net.InetAddress;

/**
 * When the state of a an ip gets changed an event of this type is distributed.
 * 
 * @author andre
 *
 */
public class IpStatus {
	
	public final static String ADDRESS_PROP_NAME = "address";
	public final static String USEDANOMALOUSLY_PROP_NAME = "usedAnomalously";
	
	
	private final String address;
	private final boolean isUsedAnomalously;
	private final boolean isUsedForSpam;
	private final boolean isUsedForFraud;
	
	
	/**
	 * Constructor.
	 * 
	 * @param address
	 * @param isUsedAnomalously
	 */
	public IpStatus(String address, boolean isUsedAnomalously){
		this.address = address;
		this.isUsedAnomalously = isUsedAnomalously;
		this.isUsedForSpam = false;
		this.isUsedForFraud = false;
		
	}
	
	/**
	 * Get the ip-address as string ({@link InetAddress#getAddress()}
	 * 
	 * @return
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Whether this address is known for unusual usage.
	 * @return
	 */
	public boolean isUsedAnomalously() {
		return isUsedAnomalously;
	}

	public boolean isUsedForSpam() {
		return isUsedForSpam;
	}

	public boolean isUsedForFraud() {
		return isUsedForFraud;
	}
	

}
