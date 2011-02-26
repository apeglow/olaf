package de.mobile.olaf.server.domain;

/**
 * Defines the type of usage of an ip-address.
 * 
 * @author andre
 *
 */
public enum IpUsageEventType {
	
	/**
	 * The ip-adress was just used.
	 */
	USE,
	
	/**
	 * Spam has been sent from this ip address.
	 */
	SPAM,
	
	/**
	 * Fraud has been posted from this address.
	 */
	FRAUD

}
