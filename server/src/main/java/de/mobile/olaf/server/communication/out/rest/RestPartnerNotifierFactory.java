package de.mobile.olaf.server.communication.out.rest;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

import de.mobile.olaf.server.communication.out.PartnerNotifier;
import de.mobile.olaf.server.communication.out.PartnerNotifierFactory;
import de.mobile.olaf.server.domain.RatedIpAddress;
import de.mobile.olaf.server.domain.PartnerNotifierType;
import de.mobile.olaf.server.domain.PartnerSite;

/**
 * Creates instances of {@link RestPartnerNotifier}.
 * 
 * @author andre
 *
 */
public class RestPartnerNotifierFactory implements PartnerNotifierFactory {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final HttpClient httpClient;
	private final SoyTofu xmlSoyTofu;
	
	/**
	 * Constructor.
	 * 
	 * @param site
	 */
	public RestPartnerNotifierFactory() {
		HttpParams httpParams = new BasicHttpParams();
		this.httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(), httpParams);
		
		String template = "IpStatusChangedEvent.xml.soy";
		URL xmlTemplateUrl = getClass().getResource(template);
		if (xmlTemplateUrl == null){
			throw new RuntimeException("xml template " + template + " not found");
		}
		
		// TODO: SoyTofu looks threadsafe but we must find out for sure
		SoyFileSet sfs = (new SoyFileSet.Builder()).add(xmlTemplateUrl).build();
	    this.xmlSoyTofu = sfs.compileToJavaObj();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mobile.olaf.server.communication.out.PartnerNotifierFactory#create(java.util.Set, java.util.Map)
	 */
	@Override
	public Set<PartnerNotifier> create(Set<PartnerSite> sites, Set<RatedIpAddress> changedStatuses) {
		String xml = createMessage(changedStatuses);
		Set<PartnerNotifier> notifiers = new HashSet<PartnerNotifier>();
		for (PartnerSite site : sites){
			if (site.getPartnerNotifierType() == PartnerNotifierType.REST){
				notifiers.add(new RestPartnerNotifier(xml, site, httpClient));
			}
		}
		
		return notifiers;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mobile.olaf.server.communication.out.PartnerNotifierFactory#getType()
	 */
	@Override
	public PartnerNotifierType getType() {
		return PartnerNotifierType.REST;
	}

	
	private String createMessage(Set<RatedIpAddress> changedIpStates) {
		SoyMapData soyMapData = new SoyMapData();
	    List<Map<String, String>> viewEvents = new ArrayList<Map<String,String>>();
	    for (RatedIpAddress ipStatus : changedIpStates){
	    	Map<String, String> viewEvent = new HashMap<String, String>();
	    	viewEvent.put("address", ipStatus.getAddress());
	    	viewEvent.put("status", ipStatus.getStatus().name());
	    	viewEvents.add(viewEvent);
	    }
	    soyMapData.put("events", viewEvents);
	    String xml = xmlSoyTofu.render("de.mobile.olaf.api.rest.xml", soyMapData, null);
	    logger.debug(xml);
	    return xml;
	}

}
