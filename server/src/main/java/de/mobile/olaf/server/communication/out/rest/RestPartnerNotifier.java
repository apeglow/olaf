package de.mobile.olaf.server.communication.out.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mobile.olaf.server.communication.out.PartnerNotifier;
import de.mobile.olaf.server.domain.PartnerSite;

/**
 * Sends rests-request for communication events.
 * 
 * @author andre
 *
 */
public class RestPartnerNotifier implements PartnerNotifier {
	private final static Logger logger = LoggerFactory.getLogger(RestPartnerNotifier.class);
	private final PartnerSite site;
	private final HttpClient httpClient;
	private final String xml;
	
	
	/**
	 * Constructor.
	 * 
	 * @param xml
	 * @param site
	 * @param httpClient
	 * 
	 */
	public RestPartnerNotifier(String xml, PartnerSite site, HttpClient httpClient){
		this.xml = xml;
		this.httpClient = httpClient;
		this.site = site;
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
		    } else if (logger.isDebugEnabled()){
		    	logger.debug("Event sent to "+site+".");
		    }
		} catch (HttpHostConnectException e) {
			logger.info("host {} not avialable", e.getHost());
		} catch (UnsupportedEncodingException e){
			throw new RuntimeException(e);
		} catch (IOException e){
			logger.error("error while sending event", e);
		}
		
	}

}
