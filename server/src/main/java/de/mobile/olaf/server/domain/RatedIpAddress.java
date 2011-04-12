package de.mobile.olaf.server.domain;

import java.net.InetAddress;

import de.mobile.common.domain.Ip4Address;
import de.mobile.olaf.api.IpAddressStatus;

/**
 * When the state of a an ip gets changed an event of this type is distributed.
 * 
 * @author andre
 *
 */
public class RatedIpAddress {
	
	public final static String ADDRESS_PROP_NAME = "address";

	public static final String STATUS_PROP_NAME = "status";

	private final Ip4Address address;

	private IpAddressStatus status;
	
	public RatedIpAddress(Ip4Address ipAddress, IpAddressStatus status){
		this.address = ipAddress;
		this.status = status;
	}
	
	public RatedIpAddress(String ipAddress, IpAddressStatus status){
		this(Ip4Address.fromString(ipAddress), status);
	}
	
	/**
	 * Get the ip-address as string ({@link InetAddress#getAddress()}
	 * 
	 * @return
	 */
	public String getAddress() {
		return address.toString();
	}
	
	public IpAddressStatus getStatus() {
		return status;
	}
	
	public void setStatus(IpAddressStatus status) {
		this.status = status;
	}

}
