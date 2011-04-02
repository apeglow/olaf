package de.mobile.olaf.server.esper.eventlistener.external;

import java.util.Iterator;

import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.server.domain.IpStatus;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

/**
 * Listens to {@link IpUsedEventType#USE} and updates the {@link IpStatus} if required.
 * 
 * @author andre
 *
 */
public class IpAddressUsedInDifferentCountriesEventListener implements UpdateListener {
	
	public final static String QUERY = "select "+IpUsedEvent.IP_PROP_NAME+", count(distinct(site.country)) as nr from "+IpUsedEvent.class.getName()+".win:time(360 min) where type='"+IpUsedEventType.USE+"'";
	
	private final EPServiceProvider epServiceProvider;
	
	public IpAddressUsedInDifferentCountriesEventListener(EPServiceProvider epServiceProvider){
		epServiceProvider.getEPAdministrator().createEPL("create window IpAddressWindow.win:time(360 min) as select * from "+IpStatus.class.getName());
		epServiceProvider.getEPAdministrator().createEPL("insert into IpAddressWindow select * from "+IpStatus.class.getName());
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
				String query = "select * from IpAddressWindow where address='"+ip+"'";
				EPOnDemandQueryResult result = epServiceProvider.getEPRuntime().executeQuery(query);
				
				IpStatus ipStatus = getCurrentIpStatus(result);
				if (ipStatus == null || !ipStatus.isUsedAnomalously()){
					ipStatus = new IpStatus(ip, true);
					
					// store new ip status
					epServiceProvider.getEPRuntime().sendEvent(ipStatus);
				} 
			}
		}
	}
	
	
	/**
	 * Converts the query result to an {@link IpStatus}.
	 * @param currentVersion
	 * @return
	 */
	private IpStatus getCurrentIpStatus(EPOnDemandQueryResult ipAddressResult) {
		Iterator<EventBean> ipAddressResultIterator = ipAddressResult.iterator();
		if (ipAddressResultIterator.hasNext()){
			EventBean eventBean = ipAddressResultIterator.next();
			String address = (String)eventBean.get(IpStatus.ADDRESS_PROP_NAME);
			Boolean usedUnusually = (Boolean)eventBean.get(IpStatus.USEDANOMALOUSLY_PROP_NAME);
			
			return new IpStatus(address, usedUnusually);
		}
		
		return null;
	}

}
