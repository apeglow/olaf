package de.mobile.olaf.server.esper.eventlistener.external;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.api.IpAddressStatus;
import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

/**
 * Rates an ip address as {@link IpAddressStatus#SUSPICIOUS} if it was used for contacting a seller in two different countries within the
 * last 6 hours.
 * 
 * @author andre
 *
 */
public class IpAddressUsedForContactInDifferentCountriesEventListener implements UpdateListener {
	
	/**
	 * Registers an instance at the service provider.
	 * 
	 * @param epServiceProvider
	 */
	public static void register(EPServiceProvider epServiceProvider){
		String query = "select "+IpUsedEvent.IP_PROP_NAME+", count(distinct(site.country)) as nr from "+IpUsedEvent.class.getName()+".win:time(360 min) where type='"+IpUsedEventType.CONTACT+"' group by "+IpUsedEvent.IP_PROP_NAME;
		EPStatement statement = epServiceProvider.getEPAdministrator().createEPL(query);
		statement.addListener(new IpAddressUsedForContactInDifferentCountriesEventListener(epServiceProvider));
	}
	
	private final EPServiceProvider epServiceProvider;
	
	private IpAddressUsedForContactInDifferentCountriesEventListener(EPServiceProvider epServiceProvider){
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
			
			if (count > 1){
				IpAddressUpdateUtil.update(ip, IpAddressStatus.SUSPICIOUS, epServiceProvider); 
			}
		}

	}

}
