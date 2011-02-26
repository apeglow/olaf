package de.mobile.olaf.server.communication.out.rest;

import java.net.URL;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import de.mobile.olaf.server.communication.out.PartnerNotifier;
import de.mobile.olaf.server.communication.out.PartnerNotifierFactoryFactory;
import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;

/**
 * Creates instances of {@link RestPartnerNotifier}.
 * 
 * @author andre
 *
 */
public class RestPartnerNotifierFactory implements PartnerNotifierFactoryFactory {
	
	private final PartnerSite site;
	private final HttpClient httpClient;
	private final URL xmlTemplateUrl;
	
	/**
	 * Constructor.
	 * 
	 * @param site
	 */
	public RestPartnerNotifierFactory(PartnerSite site){
		this.site = site;
		HttpParams httpParams = new BasicHttpParams();
		this.httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(), httpParams);
		
		String templatePath = "de/mobile/olaf/server/communication/out/rest/IpStatusChangedEvent.xml.soy";
		this.xmlTemplateUrl = Thread.currentThread().getContextClassLoader().getResource(templatePath);
		if (xmlTemplateUrl == null){
			throw new RuntimeException("xml template \""+templatePath+"\".");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.mobile.olaf.server.communication.out.AnalysisResultCommunicatorFactory#create(java.util.Map)
	 */
	@Override
	public PartnerNotifier create(Map<IpStatusChangedEvent, IpPropertyType> events) {
		return new RestPartnerNotifier(events, site, httpClient, xmlTemplateUrl);
	}

}
