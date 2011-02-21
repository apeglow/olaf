package de.mobile.olaf.server.esper;

import java.util.Iterator;

import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.server.domain.IpUsageEventType;
import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;
import de.mobile.olaf.server.esper.event.IpUsedEvent;

/**
 * Listens to {@link IpUsageEventType#USE} and updates the {@link IpStatusChangedEvent} if required.
 * 
 * @author andre
 *
 */
public class IpAddressUsageEventUpdateListener implements UpdateListener {
	
	public final static String QUERY = "select "+IpUsedEvent.IP_PROP_NAME+", count(distinct(site)) as nr from "+IpUsedEvent.class.getName()+".win:time(360 min) where type='"+IpUsageEventType.USE+"'";
	
	private final EPServiceProvider epServiceProvider;
	
	public IpAddressUsageEventUpdateListener(EPServiceProvider epServiceProvider){
		epServiceProvider.getEPAdministrator().createEPL("create window IpAddressWindow.win:time(360 min) as select * from "+IpStatusChangedEvent.class.getName());
		epServiceProvider.getEPAdministrator().createEPL("insert into IpAddressWindow select * from "+IpStatusChangedEvent.class.getName());
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
				
				IpStatusChangedEvent ipStatusChangeEvent = convertToIpStatusChangeEvent(result);
				if (ipStatusChangeEvent == null || !ipStatusChangeEvent.isUsedUnusually()){
					ipStatusChangeEvent = new IpStatusChangedEvent(ip, true);
					epServiceProvider.getEPRuntime().sendEvent(ipStatusChangeEvent);
				} 
			}
		}
	}
	
	
	/**
	 * Converts the query result to an {@link IpStatusChangedEvent}.
	 * @param currentVersion
	 * @return
	 */
	private IpStatusChangedEvent convertToIpStatusChangeEvent(EPOnDemandQueryResult currentVersion) {
		Iterator<EventBean> currentVersionIterator = currentVersion.iterator();
		if (currentVersionIterator.hasNext()){
			EventBean eventBean = currentVersionIterator.next();
			String address = (String)eventBean.get(IpStatusChangedEvent.ADDRESS_PROP_NAME);
			Boolean usedUnusually = (Boolean)eventBean.get(IpStatusChangedEvent.USEDUNUSUALLY_PROP_NAME);
			
			return new IpStatusChangedEvent(address, usedUnusually);
		}
		
		return null;
	}

}
