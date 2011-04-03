package de.mobile.olaf.server.esper.eventlistener.external;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.api.IpAddressStatus;
import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

public class IpAddressUsedInDifferentCountriesEventListener implements UpdateListener {
	
	public static void register(EPServiceProvider epServiceProvider){
		String query = "select "+IpUsedEvent.IP_PROP_NAME+", count(distinct(site.country)) as nr from "+IpUsedEvent.class.getName()+".win:time(360 min) where type='"+IpUsedEventType.USE+"'";
		EPStatement statement = epServiceProvider.getEPAdministrator().createEPL(query);
		statement.addListener(new IpAddressUsedInDifferentCountriesEventListener(epServiceProvider));
	}
	
	
	private final EPServiceProvider epServiceProvider;
	
	private IpAddressUsedInDifferentCountriesEventListener(EPServiceProvider epServiceProvider){
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
				IpAddressUpdateUtil.update(ip, IpAddressStatus.USED_ANOMALOUSLY, epServiceProvider); 
			}
		}
	}

}
