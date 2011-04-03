package de.mobile.olaf.server.esper.eventlistener.external;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.api.IpAddressStatus;
import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

/**
 * Rates the ip as {@link IpAddressStatus#SUSPICIOUS} if it was used more than 3 times within 5 minute for contacting a seller.
 * 
 * @author andre
 *
 */
public class IpAddressUsedForContactExcessivelyEventListener implements UpdateListener {
	
	/**
	 * Registers an instance at the service provider.
	 * 
	 * @param epServiceProvider
	 */
	public static void register(EPServiceProvider epServiceProvider){
		String query = "select "+IpUsedEvent.IP_PROP_NAME+", count("+IpUsedEvent.IP_PROP_NAME+") as nr from "+IpUsedEvent.class.getName()+".win:time(5 min) where type='"+IpUsedEventType.CONTACT+"' group by "+IpUsedEvent.IP_PROP_NAME;
		EPStatement statement = epServiceProvider.getEPAdministrator().createEPL(query);
		statement.addListener(new IpAddressUsedForContactExcessivelyEventListener(epServiceProvider));
	}
	
	private final EPServiceProvider epServiceProvider;
	
	private IpAddressUsedForContactExcessivelyEventListener(EPServiceProvider epServiceProvider){
		this.epServiceProvider = epServiceProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.espertech.esper.client.UpdateListener#update(com.espertech.esper.client.EventBean[], com.espertech.esper.client.EventBean[])
	 */
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (EventBean eventBean : newEvents){
			String ip = (String)eventBean.get(IpUsedEvent.IP_PROP_NAME);
			Long count = (Long)eventBean.get("nr");
			
			if (count > 3){
				IpAddressUpdateUtil.update(ip, IpAddressStatus.SUSPICIOUS, epServiceProvider); 
			}
		}
	}

}
