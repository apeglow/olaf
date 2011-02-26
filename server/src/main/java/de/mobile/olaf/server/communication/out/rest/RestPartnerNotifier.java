package de.mobile.olaf.server.communication.out.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

import de.mobile.olaf.server.communication.out.PartnerNotifier;
import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;

/**
 * Sends rests-request for communication events.
 * 
 * @author andre
 *
 */
public class RestPartnerNotifier implements PartnerNotifier {
	private final static Log logger = LogFactory.getLog(RestPartnerNotifier.class);
	private final PartnerSite site;
	private final HttpClient httpClient;
	private final URL xmlTemplateUrl;
	private final Map<IpStatusChangedEvent, IpPropertyType> events;
	
	
	/**
	 * Constructor.
	 * 
	 * @param events
	 * @param site
	 * @param httpClient
	 * @param xmlTemplateUrl
	 */
	public RestPartnerNotifier(Map<IpStatusChangedEvent, IpPropertyType> events, PartnerSite site, HttpClient httpClient, URL xmlTemplateUrl){
		this.events = events;
		this.httpClient = httpClient;
		this.site = site;
		this.xmlTemplateUrl = xmlTemplateUrl;
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.mobile.olaf.server.communication.out.AnalysisResultCommunicator#getSite()
	 */
	@Override
	public PartnerSite getSite() {
		return site;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
		    String xml = generateXml();
	
		    HttpPost post = new HttpPost(site.getAnalysisResultReceivingServiceUri());
		    post.setEntity(new StringEntity(xml));
		    int status = httpClient.execute(post, new ResponseHandler<Integer>() {

				@Override
				public Integer handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					return response.getStatusLine().getStatusCode();
				}
			});
		    
		    if (status != HttpStatus.SC_OK){
		    	logger.warn("Could not sent event to "+site.getName()+". Server returned "+status+".");
		    } else {
		    	if (logger.isDebugEnabled()){
		    		logger.debug("Event sent to "+site+".");
		    	}
		    }
		    
		} catch (UnsupportedEncodingException e){
			logger.error("could not send event", e);
		} catch (RuntimeException e){
			logger.error("error while sending event", e);
		} catch (IOException e){
			logger.error("error while sending event", e);
		}
		
	}

	private String generateXml() {
		SoyFileSet sfs = (new SoyFileSet.Builder()).add(xmlTemplateUrl).build();
	    SoyTofu tofu = sfs.compileToJavaObj();
	    
	    SoyMapData soyMapData = new SoyMapData();
	    List<Map<String, String>> viewEvents = new ArrayList<Map<String,String>>();
	    for (Entry<IpStatusChangedEvent, IpPropertyType> entry:events.entrySet()){
	    	IpStatusChangedEvent event = entry.getKey();
	    	IpPropertyType newProperty = entry.getValue();
	    	
	    	Map<String, String> viewEvent = new HashMap<String, String>();
	    	viewEvent.put("address", event.getAddress());
	    	viewEvent.put("spam", Boolean.toString(event.isUsedForSpam()));
	    	viewEvent.put("fraud", Boolean.toString(event.isUsedForFraud()));
	    	viewEvent.put("anomalously", Boolean.toString(event.isUsedAnomalously()));
	    	viewEvent.put("newProperty", newProperty.name());
	    	
	    	viewEvents.add(viewEvent);
	    }
	    
	    soyMapData.put("events", viewEvents);

	    String xml = tofu.render("de.mobile.olaf.api.rest.xml", soyMapData, null);
	    
	    return xml;
	}
	
	

}
