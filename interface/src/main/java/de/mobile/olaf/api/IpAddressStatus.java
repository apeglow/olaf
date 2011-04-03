package de.mobile.olaf.api;

/**
 * Rating status of an ip address.
 * 
 * @author andre
 *
 */
public enum IpAddressStatus {
	
	/**
	 * we have not enough information about this ip address
	 */
	UNKNOWN,
	
	/**
	 * the address was used in an not common way
	 */
	USED_ANOMALOUSLY,
	
	/**
	 * the usage of this ip address is suspicious
	 */
	SUSPICIOUS,
	
	/**
	 * a partner site announced that this ip address has been used for fraud
	 */
	USED_FOR_FRAUD,
	
	/**
	 * a partner site announced that this ip address was used for a "good" action
	 */
	KNOWN_GOOD

}
