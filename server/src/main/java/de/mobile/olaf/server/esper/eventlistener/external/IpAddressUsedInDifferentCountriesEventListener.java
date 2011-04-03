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

/**
 * Listens to {@link IpUsedEventType#USE} and updates the {@link RatedIpAddress} if required.
 * 
 * @author andre
 *
 */
public class IpAddressUsedInDifferentCountriesEventListener implements UpdateListener {
	
	public final static String QUERY = "select "+IpUsedEvent.IP_PROP_NAME+", count(distinct(site.country)) as nr from "+IpUsedEvent.class.getName()+".win:time(360 min) where type='"+IpUsedEventType.USE+"'";
	
	private final EPServiceProvider epServiceProvider;
	
	public IpAddressUsedInDifferentCountriesEventListener(EPServiceProvider epServiceProvider){
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
				String query = "select * from "+Olaf.RATED_IP_ADDRESS_WINDOW_NAME+" where address='"+ip+"'";
				EPOnDemandQueryResult result = epServiceProvider.getEPRuntime().executeQuery(query);
				
				EventBean[] ratedIpAddressEventBeans = result.getArray();
				
				if (ratedIpAddressEventBeans == null || ratedIpAddressEventBeans.length == 0){
					RatedIpAddress ratedIpAddress = new RatedIpAddress(ip, IpAddressStatus.USED_ANOMALOUSLY);
					// store new ip status
					epServiceProvider.getEPRuntime().sendEvent(ratedIpAddress);
				} else {
					
					for (EventBean ratedIpAddressEventBean: ratedIpAddressEventBeans){
						RatedIpAddress ratedIpAddress = (RatedIpAddress)ratedIpAddressEventBean.getUnderlying();
						if (ratedIpAddress.getStatus().compareTo(IpAddressStatus.USED_ANOMALOUSLY)<0){
							ratedIpAddress.setStatus(IpAddressStatus.USED_ANOMALOUSLY);
							// store new ip status
							epServiceProvider.getEPRuntime().sendEvent(ratedIpAddress);
						}
					}
					
					
				} 
			}
		}
	}
	

}
