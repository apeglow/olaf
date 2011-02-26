package de.mobile.olaf.server.esper.event;

import de.mobile.olaf.server.domain.IpUsageEventType;
import de.mobile.olaf.server.domain.PartnerSite;


public class IpUsedEvent {
	
	public final static String IP_PROP_NAME = "ip";
	
	private final String ip;
	private final String type;
	private final PartnerSite site;
	
	public IpUsedEvent(String ip, IpUsageEventType type, PartnerSite site){
		this.ip = ip;
		this.type = type.name();
		this.site = site;
	}
	
	public String getIp() {
		return ip;
	}
	
	public PartnerSite getSite() {
		return site;
	}
	
	public String getType() {
		return type;
	}

}
