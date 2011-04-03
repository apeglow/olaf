package de.mobile.olaf.server.esper.eventlistener.internal;

import java.util.HashSet;
import java.util.Set;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.server.communication.out.PartnersNotificationService;
import de.mobile.olaf.server.domain.RatedIpAddress;

/**
 * Listens to the event "ip address rated" and calls the partners notification service.
 * 
 * @author andre
 *
 */
public class IpAddressRatedEventListener implements UpdateListener {
	
	public final static String QUERY = "select * from "+RatedIpAddress.class.getName();

	private final PartnersNotificationService notificationService;
	
	public IpAddressRatedEventListener(PartnersNotificationService notificationService){
		this.notificationService = notificationService;
	}
	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		Set<RatedIpAddress> changedIpStatuses = new HashSet<RatedIpAddress>();
		
		for (EventBean eventBean : newEvents){
			RatedIpAddress ipStatus = (RatedIpAddress)eventBean.getUnderlying();
			changedIpStatuses.add(ipStatus);
		}
		
		notificationService.ipStatusChanged(changedIpStatuses);
	}

}
