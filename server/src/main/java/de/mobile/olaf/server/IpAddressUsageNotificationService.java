package de.mobile.olaf.server;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EPServiceProvider;

import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

/**
 * This service is used to notify the system about an event happening on a partner platform.
 * 
 * @author andre
 *
 */
public class IpAddressUsageNotificationService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final EPServiceProvider epServiceProvider;
	
	/**
	 * Constructor.
	 * 
	 * @param epServiceProvider
	 */
	public IpAddressUsageNotificationService(EPServiceProvider epServiceProvider){
		this.epServiceProvider =epServiceProvider;

	}
	
	/**
	 * Notify the system about an ip-address usage
	 * 
	 * @param ipAddress
	 * @param site
	 * @param eventType
	 */
	public void notify(InetAddress ipAddress, PartnerSite site, IpUsedEventType eventType){
		if (logger.isDebugEnabled()){
			logger.debug("Received "+eventType+" notification for ip-address "+ipAddress+" on site "+site+".");
		}
		
		IpUsedEvent ipUsageEvent = new IpUsedEvent(ipAddress.getHostAddress(), eventType, site);
		
		epServiceProvider.getEPRuntime().sendEvent(ipUsageEvent);
		
	}

}
