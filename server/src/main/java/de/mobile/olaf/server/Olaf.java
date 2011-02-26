package de.mobile.olaf.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import de.mobile.olaf.server.communication.in.RemoteEventNotificationWorker;
import de.mobile.olaf.server.communication.out.PartnersNotificationService;
import de.mobile.olaf.server.domain.Country;
import de.mobile.olaf.server.domain.PartnerNotifierType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.domain.PartnerSiteType;
import de.mobile.olaf.server.esper.IpAddressRatedAsAnomalouslyUsedEventListener;
import de.mobile.olaf.server.esper.IpAddressUsageEventUpdateListener;

/**
 * The OLAF application.
 * 
 * @author andre
 *
 */
public class Olaf {
	
	/**
	 * This map will be stored either in a database or a config file later.
	 */
	public final static List<PartnerSite> ipAddress2SiteMap = new ArrayList<PartnerSite>();
	static {
		try {
			ipAddress2SiteMap.add(new PartnerSite("mobile.de", Country.DE, PartnerSiteType.MOTORS_CLASSIFIED, PartnerNotifierType.REST, new URI("http://localhost:8080/olaf_rest_service")));
			ipAddress2SiteMap.add(new PartnerSite("marktplaats", Country.NL, PartnerSiteType.GENERAL_CLASSIFIED, PartnerNotifierType.REST, new URI("http://localhost:8080/olaf_rest_service")));
			ipAddress2SiteMap.add(new PartnerSite("annunci", Country.IT, PartnerSiteType.GENERAL_CLASSIFIED, PartnerNotifierType.REST, new URI("http://localhost:8080/olaf_rest_service")));
		} catch (URISyntaxException e){
			throw new RuntimeException(e);
		}
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final IpAddressUsageNotificationService ipAddressUsageNotificationService;
	private final PartnersNotificationService partnersNotificationService;
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean run = true;
	
	
	
	public static void main(String[] args) throws IOException {
		Olaf olaf = new Olaf();
		olaf.start();
	}
	
	
	
	public Olaf(){
		EPServiceProvider epServiceProvider = EPServiceProviderManager.getDefaultProvider();
		partnersNotificationService = new PartnersNotificationService(new HashSet<PartnerSite>(ipAddress2SiteMap));
		ipAddressUsageNotificationService = new IpAddressUsageNotificationService(epServiceProvider);
		
		/*
		 * Listen to ip usage events
		 */
		EPStatement unusalUsageEventStatement = epServiceProvider.getEPAdministrator().createEPL(IpAddressUsageEventUpdateListener.QUERY);
		IpAddressUsageEventUpdateListener unusalUsageStatusSettingEventUpdateListener = new IpAddressUsageEventUpdateListener(epServiceProvider);
		unusalUsageEventStatement.addListener(unusalUsageStatusSettingEventUpdateListener);
		
		/*
		 * Listen to ip address usage status change events
		 */
		EPStatement ipAddressUsedUnusallyStatement = epServiceProvider.getEPAdministrator().createEPL(IpAddressRatedAsAnomalouslyUsedEventListener.QUERY);
		IpAddressRatedAsAnomalouslyUsedEventListener ipAddressRatedAsUnsuallyUsedEventListener = new IpAddressRatedAsAnomalouslyUsedEventListener(partnersNotificationService);
		ipAddressUsedUnusallyStatement.addListener(ipAddressRatedAsUnsuallyUsedEventListener);
	}
	
	/**
	 * Starts the service.
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		
		DatagramSocket socket = new DatagramSocket(5555);
		
		while(run){
			DatagramPacket packet = new DatagramPacket( new byte[256], 256);
			socket.receive(packet);
			logger.debug("New packet received.");
			
			RemoteEventNotificationWorker notificationWorker = new RemoteEventNotificationWorker(packet, ipAddressUsageNotificationService);
			executorService.execute(notificationWorker);
		}
		
	}
	
	
	public void stop(){
		run = false;
		executorService.shutdown();
		partnersNotificationService.shutdown();
	}
	

	

}
