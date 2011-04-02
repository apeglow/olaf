package de.mobile.olaf.server.communication.out;

import java.util.Set;

import de.mobile.olaf.server.domain.IpStatus;
import de.mobile.olaf.server.domain.PartnerNotifierType;
import de.mobile.olaf.server.domain.PartnerSite;

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
	 * @param changedStatuses
	 * @return
	 */
	public Set<PartnerNotifier> create(Set<PartnerSite> sites, Set<IpStatus> changedStatuses);

	/**
	 * Get the type.
	 * 
	 * @return
	 */
	public PartnerNotifierType getType();

}
