package de.mobile.olaf.server.esper;

import java.util.HashMap;
import java.util.Map;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.server.communication.out.PartnersNotificationService;
import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.esper.event.IpStatus;

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
		Map<IpStatus, IpPropertyType> events = new HashMap<IpStatus, IpPropertyType>();
		
		for (EventBean eventBean : newEvents){
			String address = (String)eventBean.get(IpStatus.ADDRESS_PROP_NAME);
			IpStatus event = new IpStatus(address, true);
			events.put(event, IpPropertyType.ANOMALOUS_BEHAVIOUR);
		}
		
		notificationService.ipStatusChanged(events);
	}

}
