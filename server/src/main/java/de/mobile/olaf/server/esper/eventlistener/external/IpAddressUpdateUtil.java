package de.mobile.olaf.server.esper.eventlistener.external;

import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventBean;

import de.mobile.olaf.api.IpAddressStatus;
import de.mobile.olaf.server.Olaf;
import de.mobile.olaf.server.domain.RatedIpAddress;

public class IpAddressUpdateUtil {
	
	public static void update(String ipAddress, IpAddressStatus status, EPServiceProvider epServiceProvider){
		String query = "select * from "+Olaf.RATED_IP_ADDRESS_WINDOW_NAME+" where address='"+ipAddress+"'";
		EPOnDemandQueryResult result = epServiceProvider.getEPRuntime().executeQuery(query);
		
		EventBean[] ratedIpAddressEventBeans = result.getArray();
		
		if (ratedIpAddressEventBeans == null || ratedIpAddressEventBeans.length == 0){
			RatedIpAddress ratedIpAddress = new RatedIpAddress(ipAddress, status);
			// store new ip status
			epServiceProvider.getEPRuntime().sendEvent(ratedIpAddress);
		} else {
			
			for (EventBean ratedIpAddressEventBean: ratedIpAddressEventBeans){
				RatedIpAddress ratedIpAddress = (RatedIpAddress)ratedIpAddressEventBean.getUnderlying();
				if (ratedIpAddress.getStatus().compareTo(status)<0){
					ratedIpAddress.setStatus(status);
					// store new ip status
					epServiceProvider.getEPRuntime().sendEvent(ratedIpAddress);
				}
			}
		}
	}

}
