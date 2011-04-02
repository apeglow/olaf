package de.mobile.olaf.api;

/**
 * Defines the type of usage of an ip-address.
 * 
 * @author andre
 *
 */
public enum IpUsedEventType {
	
	/**
	 * The ip-address was just used.
	 */
	USE,
	
	/**
	 * ip address used for contacting a seller
	 */
	CONTACT,
	
	/**
	 * ip address used for posting or modifying an ad
	 */
	POST,
	
	/**
	 * Spam has been sent from this ip address.
	 */
	SPAM,
	
	/**
	 * Fraud has been posted from this address.
	 */
	FRAUD

}
