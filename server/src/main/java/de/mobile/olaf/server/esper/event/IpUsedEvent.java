package de.mobile.olaf.server.esper.event;

import de.mobile.common.domain.Ip4Address;
import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.server.domain.PartnerSite;


public class IpUsedEvent {
	
	public final static String IP_PROP_NAME = "ip";
	
	private final Ip4Address ip;
	private final String type;
	private final PartnerSite site;
	
	public IpUsedEvent(Ip4Address ip, IpUsedEventType type, PartnerSite site){
		this.ip = ip;
		this.type = type.name();
		this.site = site;
	}
	
	public String getIp() {
		return ip.toString();
	}
	
	public PartnerSite getSite() {
		return site;
	}
	
	public String getType() {
		return type;
	}

}
