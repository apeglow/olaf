package de.mobile.olaf.server.communication.out;

import java.util.Map;

import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;

/**
 * Creates instances of type {@link PartnerNotifier}.
 * 
 * @author andre
 *
 */
public interface PartnerNotifierFactoryFactory {
	
	public PartnerNotifier create(Map<IpStatusChangedEvent, IpPropertyType> events);

}
