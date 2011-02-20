package de.mobile.olaf.server.domain;


public class IpUsagedEvent {
	
	private final IpUsageEventType type;
	private final Site site;
	
	public IpUsagedEvent(IpUsageEventType type, Site site){
		this.type = type;
		this.site = site;
	}
	
	public Site getSite() {
		return site;
	}
	
	public IpUsageEventType getType() {
		return type;
	}

}
