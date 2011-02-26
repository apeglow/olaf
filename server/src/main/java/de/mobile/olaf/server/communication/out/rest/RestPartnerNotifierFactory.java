package de.mobile.olaf.server.communication.out.rest;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;

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
	public RestPartnerNotifierFactory(){
		HttpParams httpParams = new BasicHttpParams();
		this.httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(), httpParams);
		
		String templatePath = "de/mobile/olaf/server/communication/out/rest/IpStatusChangedEvent.xml.soy";
		URL xmlTemplateUrl = Thread.currentThread().getContextClassLoader().getResource(templatePath);
		if (xmlTemplateUrl == null){
			throw new RuntimeException("xml template \""+templatePath+"\".");
		}
		
		// TODO: SoyTofu looks threadsafe but we must find out for sure
		SoyFileSet sfs = (new SoyFileSet.Builder()).add(xmlTemplateUrl).build();
	    this.xmlSoyTofu = sfs.compileToJavaObj();
	}

	/*
	 * (non-Javadoc)
	 * @see de.mobile.olaf.server.communication.out.PartnerNotifierFactory#create(de.mobile.olaf.server.domain.PartnerSite, java.lang.Object)
	 */
	@Override
	public PartnerNotifier create(PartnerSite site, Object message) {
		String xml = (String)message;
		return new RestPartnerNotifier(xml, site, httpClient);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mobile.olaf.server.communication.out.PartnerNotifierFactory#createMessage(java.util.Map)
	 */
	@Override
	public Object createMessage(Map<IpStatusChangedEvent, IpPropertyType> events) {
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

	    String xml = xmlSoyTofu.render("de.mobile.olaf.api.rest.xml", soyMapData, null);
	    
	    logger.debug(xml);
	    
	    return xml;
	}

}
