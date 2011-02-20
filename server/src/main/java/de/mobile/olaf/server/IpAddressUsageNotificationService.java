package de.mobile.olaf.server;

import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.mobile.olaf.server.domain.IpUsageEventType;
import de.mobile.olaf.server.domain.Site;

public class IpAddressUsageNotificationService {
	private final Log logger = LogFactory.getLog(getClass());
	
	public void notify(InetAddress ipAddress, Site site, IpUsageEventType eventType){
		logger.debug("Received "+eventType+" notification for ip-address "+ipAddress+" on site "+site+".");
	}

}
