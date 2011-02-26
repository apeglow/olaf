package de.mobile.olaf.server.domain;

/**
 * This is the set of properties found by the analysis
 * an ip-address has.
 * 
 * 
 * @author andre
 *
 */
public enum IpPropertyType {
	
	/**
	 * Indicates that an ip-address was used anomalously. (e.g. in different countries within a short time)
	 */
	ANOMALOUS_BEHAVIOUR,
	
	/**
	 * A partner announced that from this ip-address fraudulent actions have be committed.
	 */
	FRAUD_POSTED,
	
	/**
	 * A partner announced that spam was sent from this ip-address.
	 */
	SPAM_SENT

}
