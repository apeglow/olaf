package de.mobile.olaf.server.esper.event;

import java.net.InetAddress;

/**
 * When the state of a an ip gets changed an event of this type is distributed.
 * 
 * @author andre
 *
 */
public class IpStatusChangedEvent {
	
	public final static String ADDRESS_PROP_NAME = "address";
	public final static String USEDUNUSUALLY_PROP_NAME = "usedUnusually";
	
	
	private final String address;
	private final boolean usedUnusually;
	
	
	/**
	 * Constructor.
	 * 
	 * @param address
	 * @param usedUnusually
	 */
	public IpStatusChangedEvent(String address, boolean usedUnusually){
		this.address = address;
		this.usedUnusually = usedUnusually;
		
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
	public boolean isUsedUnusually() {
		return usedUnusually;
	}
	

}
