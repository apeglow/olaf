package de.mobile.olaf.server.communication.out;

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
import de.mobile.olaf.server.communication.out.udp.UdpPartnerNotifierFactory;
import de.mobile.olaf.server.domain.IpPropertyType;
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
	
	/**
	 * Number of maximal threads for communication of analysis results per site.
	 */
	private final static int THREADS_PER_SITE = 5;
	
	private static PartnerNotifierFactoryFactory createCommunicatorFactory(PartnerSite site){
		switch (site.getAnalysisResultCommunicatorType()){
		case UDP:
			return new UdpPartnerNotifierFactory(site);
		case REST:
			return new RestPartnerNotifierFactory(site);
		}
		
		throw new UnsupportedOperationException();
	}
	
	
	/** class members **/
	
	private final Map<PartnerNotifierFactoryFactory, ExecutorService> communicators;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	/**
	 * Constructor.
	 * 
	 * @param sites
	 */
	public PartnersNotificationService(Set<PartnerSite> sites){
		communicators = new ConcurrentHashMap<PartnerNotifierFactoryFactory, ExecutorService>();
		
		for (PartnerSite site:sites){
			PartnerNotifierFactoryFactory communicatorFactory = createCommunicatorFactory(site);
			ExecutorService executorService = new ThreadPoolExecutor(1, THREADS_PER_SITE, 50000L,   TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1));
		
			communicators.put(communicatorFactory, executorService);
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
		for (Entry<PartnerNotifierFactoryFactory, ExecutorService> entry:communicators.entrySet()){
			PartnerNotifierFactoryFactory analysisResultCommunicatorFactory = entry.getKey();
			ExecutorService executorService = entry.getValue();
			
			PartnerNotifier communicator = analysisResultCommunicatorFactory.create(events);
			try {
				executorService.execute(communicator);
			} catch (RejectedExecutionException e){
				logger.warn("Could not send ip-address-status-change to \""+communicator.getSite()+"\". Communicators pool is full.");
			}
		}

	}
	
	/**
	 * This methods stops all communication threads. Must be called on system shutdown. 
	 * After having called this method this instance cannot be used any longer.
	 */
	public void shutdown(){
		for (ExecutorService service : communicators.values()){
			service.shutdown();
		}
	}
	
	

}
