package de.mobile.olaf.server;

import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import de.mobile.olaf.server.domain.IpUsageEventType;
import de.mobile.olaf.server.domain.Site;
import de.mobile.olaf.server.esper.IpAddressRatedAsUnsuallyUsedEventListener;
import de.mobile.olaf.server.esper.IpAddressUsageEventUpdateListener;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

/**
 * Application interface.
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
	 * 
	 */
	public IpAddressUsageNotificationService(){
		epServiceProvider = EPServiceProviderManager.getDefaultProvider();
		
		/*
		 * Listen to ip usage events
		 */
		EPStatement unusalUsageEventStatement = epServiceProvider.getEPAdministrator().createEPL(IpAddressUsageEventUpdateListener.QUERY);
		IpAddressUsageEventUpdateListener unusalUsageStatusSettingEventUpdateListener = new IpAddressUsageEventUpdateListener(epServiceProvider);
		unusalUsageEventStatement.addListener(unusalUsageStatusSettingEventUpdateListener);
		
		/*
		 * Listen to ip address usage status change events
		 */
		EPStatement ipAddressUsedUnusallyStatement = epServiceProvider.getEPAdministrator().createEPL(IpAddressRatedAsUnsuallyUsedEventListener.QUERY);
		IpAddressRatedAsUnsuallyUsedEventListener ipAddressRatedAsUnsuallyUsedEventListener = new IpAddressRatedAsUnsuallyUsedEventListener();
		ipAddressUsedUnusallyStatement.addListener(ipAddressRatedAsUnsuallyUsedEventListener);
	}
	
	/**
	 * Notify the system about an ip-address usage
	 * 
	 * @param ipAddress
	 * @param site
	 * @param eventType
	 */
	public void notify(InetAddress ipAddress, Site site, IpUsageEventType eventType){
		if (logger.isInfoEnabled()){
			logger.info("Received "+eventType+" notification for ip-address "+ipAddress+" on site "+site+".");
		}
		
		IpUsedEvent ipUsageEvent = new IpUsedEvent(ipAddress.getHostAddress(), eventType, site);
		
		epServiceProvider.getEPRuntime().sendEvent(ipUsageEvent);
		
	}

}
