package de.mobile.olaf.server.communication.out;

import de.mobile.olaf.server.domain.PartnerSite;


/**
 * Notifies a concrete partner site about a concrete set of events
 * 
 * @author andre
 *
 */
public interface PartnerNotifier extends Runnable {
	
	/**
	 * Get the site to be notified.
	 * 
	 * @return
	 */
	public PartnerSite getSite();

}
