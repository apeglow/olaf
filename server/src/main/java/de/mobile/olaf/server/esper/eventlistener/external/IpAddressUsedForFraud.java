package de.mobile.olaf.server.esper.eventlistener.external;

import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.api.IpAddressStatus;
import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.server.Olaf;
import de.mobile.olaf.server.domain.RatedIpAddress;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

public class IpAddressUsedForFraud implements UpdateListener {
	public final static String QUERY = "select * from "+IpUsedEvent.class.getName()+".win:time(360 min) where type='"+IpUsedEventType.FRAUD+"'";
	
	private final EPServiceProvider epServiceProvider;
	
	public IpAddressUsedForFraud(EPServiceProvider epServiceProvider){
		this.epServiceProvider = epServiceProvider;
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (EventBean eventBean : newEvents){
			String ip = (String)eventBean.get(IpUsedEvent.IP_PROP_NAME);
			String query = "select * from "+Olaf.RATED_IP_ADDRESS_WINDOW_NAME+" where address='"+ip+"'";
			EPOnDemandQueryResult result = epServiceProvider.getEPRuntime().executeQuery(query);
			
			EventBean[] ratedIpAddressEventBeans = result.getArray();
			if (ratedIpAddressEventBeans == null || ratedIpAddressEventBeans.length == 0){
				RatedIpAddress ratedIpAddress = new RatedIpAddress(ip, IpAddressStatus.USED_FOR_FRAUD);
				epServiceProvider.getEPRuntime().sendEvent(ratedIpAddress);
			} else {
				for (EventBean ratedIpAddressEventBean : ratedIpAddressEventBeans){
					RatedIpAddress ratedIpAddress = (RatedIpAddress)ratedIpAddressEventBean.getUnderlying();
					if (ratedIpAddress.getStatus().compareTo(IpAddressStatus.USED_FOR_FRAUD)<0){
						ratedIpAddress.setStatus(IpAddressStatus.USED_FOR_FRAUD);
						epServiceProvider.getEPRuntime().sendEvent(ratedIpAddress);
					}
				}
			}
			
		}
	}

}
