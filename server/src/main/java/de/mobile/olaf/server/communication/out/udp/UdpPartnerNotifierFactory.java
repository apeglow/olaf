package de.mobile.olaf.server.communication.out.udp;

import java.util.Map;

import de.mobile.olaf.server.communication.out.PartnerNotifier;
import de.mobile.olaf.server.communication.out.PartnerNotifierFactoryFactory;
import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;

/**
 * Communicates by using udp packages.
 * 
 * @author andre
 *
 */
public class UdpPartnerNotifierFactory implements PartnerNotifierFactoryFactory {
	
	private final PartnerSite site;
	
	/**
	 * Constructor.
	 * 
	 * @param site
	 */
	public UdpPartnerNotifierFactory(PartnerSite site){
		this.site = site;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mobile.olaf.server.communication.out.AnalysisResultCommunicatorFactory#create(java.util.Map)
	 */
	@Override
	public PartnerNotifier create(Map<IpStatusChangedEvent, IpPropertyType> events) {
		return new UdpPartnerNotifier(site);
	}

}
