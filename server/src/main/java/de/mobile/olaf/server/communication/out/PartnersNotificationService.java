package de.mobile.olaf.server.communication.out;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mobile.olaf.server.communication.out.rest.RestPartnerNotifierFactory;
import de.mobile.olaf.server.domain.IpPropertyType;
import de.mobile.olaf.server.domain.PartnerNotifierType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.esper.event.IpStatusChangedEvent;

/**
 * This service notifies the partners about status changes of ip addresses.
 * 
 * @author andre
 *
 */
public class PartnersNotificationService {
	
	/** static stuff **/
	
	private final static Map<PartnerNotifierType, PartnerNotifierFactory> PARTNER_NOTIFIER_FACTORIES = new HashMap<PartnerNotifierType, PartnerNotifierFactory>();
	static {
		PARTNER_NOTIFIER_FACTORIES.put(PartnerNotifierType.REST, new RestPartnerNotifierFactory());
	}
	
	/**
	 * Number of maximal threads for communication of analysis results per site.
	 */
	private final static int THREADS_PER_SITE = 5;
	
	/** class members **/
	
	private final Map<PartnerSite, ExecutorService> partnerNotifiers;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	/**
	 * Constructor.
	 * 
	 * @param sites
	 */
	public PartnersNotificationService(Set<PartnerSite> sites){
		partnerNotifiers = new ConcurrentHashMap<PartnerSite, ExecutorService>();
		
		for (PartnerSite site:sites){
			ExecutorService executorService = new ThreadPoolExecutor(1, THREADS_PER_SITE, 50000L,   TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1));
		
			partnerNotifiers.put(site, executorService);
		}
	}
	
	
	/**
	 * To be called when the status of an ip-address changed.
	 * 
	 * @param events
	 * 
	 */
	public void ipStatusChanged(Map<IpStatusChangedEvent, IpPropertyType> events){
		if (logger.isDebugEnabled()){
			logger.debug("ip status changed. informing partners.");
		}
		
		/*
		 * compute the message for every notifier type
		 */
		Map<PartnerNotifierType, Object> messages = new HashMap<PartnerNotifierType, Object>();
		for (Entry<PartnerNotifierType, PartnerNotifierFactory> entry : PARTNER_NOTIFIER_FACTORIES.entrySet()){
			PartnerNotifierType partnerNotifierType = entry.getKey();
			PartnerNotifierFactory partnerNotifierFactory = entry.getValue();
			
			Object message = partnerNotifierFactory.createMessage(events);
			messages.put(partnerNotifierType, message);
		}
		
		/*
		 * distribute the message to the partners
		 */
		for (Entry<PartnerSite, ExecutorService> entry:partnerNotifiers.entrySet()){
			PartnerSite site = entry.getKey();
			ExecutorService executorService = entry.getValue();
			
			PartnerNotifierType partnerNotifierType = site.getPartnerNotifierType();
			PartnerNotifierFactory partnerNotifierFactory = PARTNER_NOTIFIER_FACTORIES.get(partnerNotifierType);
			Object message = messages.get(partnerNotifierType);
			PartnerNotifier partnerNotifier = partnerNotifierFactory.create(site, message);
			try {
				executorService.execute(partnerNotifier);
			} catch (RejectedExecutionException e){
				logger.warn("Could not send ip-address-status-change to \""+partnerNotifier.getSite()+"\". Communicators pool is full.");
			}
		}

	}
	
	/**
	 * This methods stops all communication threads. Must be called on system shutdown. 
	 * After having called this method this instance cannot be used any longer.
	 */
	public void shutdown(){
		for (ExecutorService service : partnerNotifiers.values()){
			service.shutdown();
		}
	}
	
	

}
