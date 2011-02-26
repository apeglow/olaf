package de.mobile.olaf.server.domain;

import java.net.URI;


/**
 * Describes a partner site. Partner notify OLAF about events
 * happening on their sites and get real time information about 
 * analysis results.
 * 
 * @author andre
 *
 */
public class PartnerSite {
	
	private final String name;
	private final Country country;
	private final PartnerSiteType type;
	private final PartnerNotifierType analysisResultCommunicatorType;
	private final URI analysisResultReceivingServiceUri;
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param country
	 * @param type
	 * @param analysisResultCommunicatorType
	 * @param analysisResultReceivingServiceUri
	 */
	public PartnerSite(String name, Country country, PartnerSiteType type, PartnerNotifierType analysisResultCommunicatorType, URI analysisResultReceivingServiceUri){
		this.name = name;
		this.country = country;
		this.type = type;
		this.analysisResultCommunicatorType = analysisResultCommunicatorType;
		this.analysisResultReceivingServiceUri = analysisResultReceivingServiceUri;
	}
	
	public Country getCountry() {
		return country;
	}
	
	public String getName() {
		return name;
	}
	
	public PartnerSiteType getType() {
		return type;
	}
	
	public PartnerNotifierType getPartnerNotifierType() {
		return analysisResultCommunicatorType;
	}
	
	public URI getAnalysisResultReceivingServiceUri() {
		return analysisResultReceivingServiceUri;
	}
	
	@Override
	public String toString() {
		return name + " ("+type+" in "+country+")";
	}
	

}
