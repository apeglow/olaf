package de.mobile.olaf.server.communication.out.udp;

import java.util.Map;
import java.util.Set;

import de.mobile.olaf.server.communication.out.PartnerNotifier;
import de.mobile.olaf.server.communication.out.PartnerNotifierFactory;
import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.domain.PartnerNotifierType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.esper.event.IpStatus;

/**
 * Communicates by using udp packages.
 * 
 * @author andre
 *
 */
public class UdpPartnerNotifierFactory implements PartnerNotifierFactory {
	


	@Override
	public Set<PartnerNotifier> create(Set<PartnerSite> sites,
			Map<IpStatus, IpPropertyType> events) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PartnerNotifierType getType() {
		return PartnerNotifierType.UDP;
	}
}
