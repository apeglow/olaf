package de.mobile.olaf.server.communication.out.udp;

import java.util.Map;

import de.mobile.olaf.server.communication.out.PartnerNotifier;
import de.mobile.olaf.server.communication.out.PartnerNotifierFactory;
import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;

/**
 * Communicates by using udp packages.
 * 
 * @author andre
 *
 */
public class UdpPartnerNotifierFactory implements PartnerNotifierFactory {
	


	@Override
	public PartnerNotifier create(PartnerSite site, Object message) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object createMessage(Map<IpStatusChangedEvent, IpPropertyType> events) {
		// TODO Auto-generated method stub
		return null;
	}

}
