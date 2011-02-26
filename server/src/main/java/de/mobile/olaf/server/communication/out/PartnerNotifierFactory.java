package de.mobile.olaf.server.communication.out;

import java.util.Map;
import java.util.Set;

import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.domain.PartnerNotifierType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;

/**
 * Creates instances of type {@link PartnerNotifier}.
 * 
 * @author andre
 *
 */
public interface PartnerNotifierFactory {
	
	/**
	 * Create new notifiers for the sites of the managed type. 
	 * 
	 * @param sites 
	 * @param events
	 * @return
	 */
	public Set<PartnerNotifier> create(Set<PartnerSite> sites, Map<IpStatusChangedEvent, IpPropertyType> events);

	/**
	 * Get the type.
	 * 
	 * @return
	 */
	public PartnerNotifierType getType();

}
