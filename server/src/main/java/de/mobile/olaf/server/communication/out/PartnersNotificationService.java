package de.mobile.olaf.server.communication.out;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mobile.olaf.server.communication.out.rest.RestPartnerNotifierFactory;
import de.mobile.olaf.server.domain.RatedIpAddress;
import de.mobile.olaf.server.domain.PartnerNotifierType;
import de.mobile.olaf.server.domain.PartnerSite;

/**
 * This service notifies the partners about status changes of ip addresses.
 * 
 * @author andre
 *
 */
public class PartnersNotificationService {
	
	/** static stuff **/
	
	private final static Set<PartnerNotifierFactory> PARTNER_NOTIFIER_FACTORIES = new HashSet<PartnerNotifierFactory>();
	static {
		PARTNER_NOTIFIER_FACTORIES.add(new RestPartnerNotifierFactory());
	}
	
	/**
	 * Number of maximal threads for communication of analysis results per site.
	 */
	private final static int THREADS_PER_SITE = 5;
	
	
	/** class members **/
	
	private final Map<PartnerNotifierType, Map<PartnerSite,ExecutorService>> partnerSites;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	/**
	 * Constructor.
	 * 
	 * @param sites
	 */
	public PartnersNotificationService(Collection<PartnerSite> sites){
		partnerSites = new ConcurrentHashMap<PartnerNotifierType, Map<PartnerSite, ExecutorService>>();
		
		for (PartnerSite site:sites) {
			ExecutorService executorService = new ThreadPoolExecutor(1, THREADS_PER_SITE, 50000L,   TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100));
			Map<PartnerSite, ExecutorService> sitesWithExecutors = partnerSites.get(site.getPartnerNotifierType());
			if (sitesWithExecutors == null){
				sitesWithExecutors = new ConcurrentHashMap<PartnerSite, ExecutorService>();
				partnerSites.put(site.getPartnerNotifierType(), sitesWithExecutors);
			}
			sitesWithExecutors.put(site, executorService);
		}
	}
	
	
	/**
	 * To be called when the status of an ip-address changed.
	 * 
	 * @param events
	 * 
	 */
	public void ipStatusChanged(Set<RatedIpAddress> changedIpStatuses){
		if (logger.isDebugEnabled()){
			logger.debug("ip status changed. informing partners.");
		}
		
		for (PartnerNotifierFactory partnerNotifierFactory : PARTNER_NOTIFIER_FACTORIES){
			Map<PartnerSite, ExecutorService> partnersWithExecutors = partnerSites.get(partnerNotifierFactory.getType());
			
			Set<PartnerNotifier> partnerNotifiers = partnerNotifierFactory.create(partnersWithExecutors.keySet(), changedIpStatuses);
			for (PartnerNotifier partnerNotifier : partnerNotifiers){
				ExecutorService executorService = partnersWithExecutors.get(partnerNotifier.getSite());
				try {
					executorService.execute(partnerNotifier);
				} catch (RejectedExecutionException e){
					logger.warn("Could not send ip-address-status-change to \""+partnerNotifier.getSite()+"\". Communicators pool is full.");
				}
			}
		}
	}
	
	/**
	 * This methods stops all communication threads. Must be called on system shutdown. 
	 * After having called this method this instance cannot be used any longer.
	 */
	public void shutdown(){
		for (Map<PartnerSite, ExecutorService> siteWithExecutor : partnerSites.values()){
			for (ExecutorService executorService : siteWithExecutor.values()){
				executorService.shutdown();
			}
		}
	}
	
	

}
