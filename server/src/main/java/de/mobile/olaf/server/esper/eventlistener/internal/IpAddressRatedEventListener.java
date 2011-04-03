package de.mobile.olaf.server.esper.eventlistener.internal;

import java.util.HashSet;
import java.util.Set;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
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
	
	public static void register(EPServiceProvider epServiceProvider, PartnersNotificationService partnersNotificationService){
		String query = "select * from "+RatedIpAddress.class.getName();
		EPStatement statement = epServiceProvider.getEPAdministrator().createEPL(query);
		statement.addListener(new IpAddressRatedEventListener(partnersNotificationService));
	}
	

	private final PartnersNotificationService notificationService;
	
	private IpAddressRatedEventListener(PartnersNotificationService notificationService){
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
