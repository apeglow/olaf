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
	 * Fraud has been posted from this address.
	 */
	FRAUD,
	
	/**
	 * ip address has been used for a doubtless "good" action.
	 */
	KNOWN_GOOD

}
