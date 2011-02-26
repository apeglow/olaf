package de.mobile.olaf.server;

import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import de.mobile.olaf.server.communication.out.PartnersNotificationService;
import de.mobile.olaf.server.domain.IpUsageEventType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.esper.IpAddressRatedAsAnomalouslyUsedEventListener;
import de.mobile.olaf.server.esper.IpAddressUsageEventUpdateListener;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

/**
 * This service is used to notify the system about an event happening on a parnter platform.
 * 
 * @author andre
 *
 */
public class IpAddressUsageNotificationService {
	private final Log logger = LogFactory.getLog(getClass());
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
	public void notify(InetAddress ipAddress, PartnerSite site, IpUsageEventType eventType){
		if (logger.isInfoEnabled()){
			logger.info("Received "+eventType+" notification for ip-address "+ipAddress+" on site "+site+".");
		}
		
		IpUsedEvent ipUsageEvent = new IpUsedEvent(ipAddress.getHostAddress(), eventType, site);
		
		epServiceProvider.getEPRuntime().sendEvent(ipUsageEvent);
		
	}

}
