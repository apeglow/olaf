package de.mobile.olaf.server.communication.out;

import java.util.Map;

import de.mobile.olaf.server.domain.IpPropertyType;
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
	 * Create a new instance of a notifier. It get the message create by
	 * {@link #createMessage(Map)}.
	 * 
	 * @param site
	 * @param message
	 * @return
	 */
	public PartnerNotifier create(PartnerSite site, Object message);

	/**
	 * Create a message for this event set. 
	 * 
	 * @param events
	 * @return
	 */
	public Object createMessage(Map<IpStatusChangedEvent, IpPropertyType> events);

}
