package de.mobile.olaf.server.esper;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;

public class IpAddressRatedAsUnsuallyUsedEventListener implements UpdateListener {
	
	public final static String QUERY = "select address from "+IpStatusChangedEvent.class.getName()+" where "+IpStatusChangedEvent.USEDUNUSUALLY_PROP_NAME+"=true";

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (EventBean eventBean : newEvents){
			System.out.println("ip used unusally: "+eventBean.get(IpStatusChangedEvent.ADDRESS_PROP_NAME));
		}
	}

}
