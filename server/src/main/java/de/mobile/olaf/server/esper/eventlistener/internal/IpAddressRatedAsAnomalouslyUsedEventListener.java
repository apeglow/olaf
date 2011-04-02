package de.mobile.olaf.server.esper.eventlistener.internal;

import java.util.HashSet;
import java.util.Set;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.server.communication.out.PartnersNotificationService;
import de.mobile.olaf.server.domain.IpStatus;

/**
 * Listens to the event "ip address rated as anomalously used" and calls the partners notification service.
 * 
 * @author andre
 *
 */
public class IpAddressRatedAsAnomalouslyUsedEventListener implements UpdateListener {
	
	public final static String QUERY = "select address from "+IpStatus.class.getName()+" where "+IpStatus.USEDANOMALOUSLY_PROP_NAME+"=true";

	private final PartnersNotificationService notificationService;
	
	public IpAddressRatedAsAnomalouslyUsedEventListener(PartnersNotificationService notificationService){
		this.notificationService = notificationService;
	}
	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		Set<IpStatus> changedIpStatuses = new HashSet<IpStatus>();
		
		for (EventBean eventBean : newEvents){
			String address = (String)eventBean.get(IpStatus.ADDRESS_PROP_NAME);
			IpStatus event = new IpStatus(address, true);
			changedIpStatuses.add(event);
		}
		
		notificationService.ipStatusChanged(changedIpStatuses);
	}

}
