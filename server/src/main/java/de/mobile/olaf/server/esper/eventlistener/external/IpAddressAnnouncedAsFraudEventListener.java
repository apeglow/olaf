package de.mobile.olaf.server.esper.eventlistener.external;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.api.IpAddressStatus;
import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

/**
 * Rates an ip address as {@link IpAddressStatus#USED_FOR_FRAUD} if announced by a partner.
 * 
 * @author andre
 *
 */
public class IpAddressAnnouncedAsFraudEventListener implements UpdateListener {
	
	/**
	 * Registers an instance at the service provider.
	 * 
	 * @param epServiceProvider
	 */
	public static void register(EPServiceProvider epServiceProvider){
		String query = "select distinct(ip) from " + IpUsedEvent.class.getName() + " where type='" + IpUsedEventType.FRAUD + "'";
		EPStatement statement = epServiceProvider.getEPAdministrator().createEPL(query);
		statement.addListener(new IpAddressAnnouncedAsFraudEventListener(epServiceProvider));
	}
	
	private final EPServiceProvider epServiceProvider;
	
	private IpAddressAnnouncedAsFraudEventListener(EPServiceProvider epServiceProvider){
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
			IpAddressUpdateUtil.update(ip, IpAddressStatus.USED_FOR_FRAUD, epServiceProvider); 
		}
	}
}
