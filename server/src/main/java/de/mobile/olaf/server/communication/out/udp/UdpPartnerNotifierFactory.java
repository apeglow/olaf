package de.mobile.olaf.server.communication.out.udp;

import java.util.Set;

import de.mobile.olaf.server.communication.out.PartnerNotifier;
import de.mobile.olaf.server.communication.out.PartnerNotifierFactory;
import de.mobile.olaf.server.domain.RatedIpAddress;
import de.mobile.olaf.server.domain.PartnerNotifierType;
import de.mobile.olaf.server.domain.PartnerSite;

/**
 * Communicates by using udp packages.
 * 
 * @author andre
 *
 */
public class UdpPartnerNotifierFactory implements PartnerNotifierFactory {
	


	@Override
	public Set<PartnerNotifier> create(Set<PartnerSite> sites,
			Set<RatedIpAddress> changedStatuses) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PartnerNotifierType getType() {
		return PartnerNotifierType.UDP;
	}
}
