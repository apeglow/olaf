package de.mobile.olaf.server.domain;

/**
 * Partners may implement their notification server
 * with several protocols. This enum is the list of
 * all available protocols.
 * 
 * @author andre
 *
 */
public enum PartnerNotifierType {
	
	/**
	 * use udp messages
	 */
	UDP,
	
	/**
	 * use REST-compliant http messages
	 */
	REST

}
