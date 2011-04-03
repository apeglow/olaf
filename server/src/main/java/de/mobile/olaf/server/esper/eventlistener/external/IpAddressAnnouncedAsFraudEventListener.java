package de.mobile.olaf.server.esper.eventlistener.external;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.api.IpAddressStatus;
import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

public class IpAddressAnnouncedAsFraudEventListener implements UpdateListener {
	
	public static void register(EPServiceProvider epServiceProvider){
		String query = "select * from "+IpUsedEvent.class.getName()+".win:time(360 min) where type='"+IpUsedEventType.FRAUD+"'";
		EPStatement statement = epServiceProvider.getEPAdministrator().createEPL(query);
		statement.addListener(new IpAddressAnnouncedAsFraudEventListener(epServiceProvider));
	}
	
	private final EPServiceProvider epServiceProvider;
	
	public IpAddressAnnouncedAsFraudEventListener(EPServiceProvider epServiceProvider){
		this.epServiceProvider = epServiceProvider;
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (EventBean eventBean : newEvents){
			String ip = (String)eventBean.get(IpUsedEvent.IP_PROP_NAME);
			
			IpAddressUpdateUtil.update(ip, IpAddressStatus.USED_FOR_FRAUD, epServiceProvider); 
			
		}
	}

}
