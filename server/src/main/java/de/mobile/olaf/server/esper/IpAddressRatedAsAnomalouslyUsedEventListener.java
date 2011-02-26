package de.mobile.olaf.server.esper;

import java.util.HashMap;
import java.util.Map;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.server.communication.out.PartnersNotificationService;
import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;

/**
 * Listens to the event "ip address rated as anomalously used" and calls the partners notification service.
 * 
 * @author andre
 *
 */
public class IpAddressRatedAsAnomalouslyUsedEventListener implements UpdateListener {
	
	public final static String QUERY = "select address from "+IpStatusChangedEvent.class.getName()+" where "+IpStatusChangedEvent.USEDANOMALOUSLY_PROP_NAME+"=true";

	private final PartnersNotificationService notificationService;
	
	public IpAddressRatedAsAnomalouslyUsedEventListener(PartnersNotificationService notificationService){
		this.notificationService = notificationService;
	}
	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		Map<IpStatusChangedEvent, IpPropertyType> events = new HashMap<IpStatusChangedEvent, IpPropertyType>();
		
		for (EventBean eventBean : newEvents){
			String address = (String)eventBean.get(IpStatusChangedEvent.ADDRESS_PROP_NAME);
			IpStatusChangedEvent event = new IpStatusChangedEvent(address, true);
			events.put(event, IpPropertyType.ANOMALOUS_BEHAVIOUR);
		}
		
		notificationService.ipStatusChanged(events);
	}

}
