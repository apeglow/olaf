package de.mobile.olaf.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

import de.mobile.olaf.server.communication.in.RemoteEventNotificationWorker;
import de.mobile.olaf.server.communication.out.PartnersNotificationService;
import de.mobile.olaf.server.domain.Country;
import de.mobile.olaf.server.domain.PartnerNotifierType;
import de.mobile.olaf.server.domain.PartnerSite;
import de.mobile.olaf.server.domain.PartnerSiteType;
import de.mobile.olaf.server.domain.RatedIpAddress;
import de.mobile.olaf.server.esper.eventlistener.external.IpAddressAnnouncedAsFraudEventListener;
import de.mobile.olaf.server.esper.eventlistener.external.IpAddressUsedForContactExcessivelyEventListener;
import de.mobile.olaf.server.esper.eventlistener.external.IpAddressUsedForContactInDifferentCountriesEventListener;
import de.mobile.olaf.server.esper.eventlistener.external.IpAddressUsedForPostingInDifferentCountriesEventListener;
import de.mobile.olaf.server.esper.eventlistener.external.IpAddressUsedInDifferentCountriesEventListener;
import de.mobile.olaf.server.esper.eventlistener.internal.IpAddressRatedEventListener;

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
	public final static List<PartnerSite> ipAddress2SiteMap = Arrays.asList(
			new PartnerSite("mobile.de", Country.DE, PartnerSiteType.MOTORS_CLASSIFIED, PartnerNotifierType.REST, URI.create("http://localhost:8080/olaf_rest_service")),
			new PartnerSite("marktplaats", Country.NL, PartnerSiteType.GENERAL_CLASSIFIED, PartnerNotifierType.REST, URI.create("http://localhost:8080/olaf_rest_service")),
			new PartnerSite("annunci", Country.IT, PartnerSiteType.GENERAL_CLASSIFIED, PartnerNotifierType.REST, URI.create("http://localhost:8080/olaf_rest_service")));//
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final IpAddressUsageNotificationService ipAddressUsageNotificationService;
	
	private final PartnersNotificationService partnersNotificationService;
	
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean run = true;
	
	public static void main(String[] args) throws IOException {
		Olaf olaf = new Olaf();
		olaf.start();
	}
	
	public Olaf() {
		EPServiceProvider epServiceProvider = EPServiceProviderManager.getDefaultProvider();
		partnersNotificationService = new PartnersNotificationService(ipAddress2SiteMap);
		ipAddressUsageNotificationService = new IpAddressUsageNotificationService(epServiceProvider);
		
		epServiceProvider //
			.getEPAdministrator() //
			.createEPL("create window RatedIpAddressWindow.win:time(360 min) as " +
					   "select * from " + RatedIpAddress.class.getName());
		
		epServiceProvider //
			.getEPAdministrator() //
			.createEPL("insert into RatedIpAddressWindow select * from " + RatedIpAddress.class.getName());
		
		/*
		 * listen to ip address rated events
		 */
		//IpAddressRatedEventListener.register(epServiceProvider, partnersNotificationService);
		
		/*
		 * Listen to ip usage in different countries events
		 */
		IpAddressUsedInDifferentCountriesEventListener.register(epServiceProvider);
		
		/*
		 * Listen to fraud announcments
		 */
		IpAddressAnnouncedAsFraudEventListener.register(epServiceProvider);
		
		/*
		 * Listen to contact events in different countries 
		 */
		IpAddressUsedForContactInDifferentCountriesEventListener.register(epServiceProvider);
		
		/*
		 *  used excessively
		 */
		IpAddressUsedForContactExcessivelyEventListener.register(epServiceProvider);
		
		/*
		 * used for posting in different countries
		 */
		IpAddressUsedForPostingInDifferentCountriesEventListener.register(epServiceProvider);
	}
	
	int numMessages;
	/**
	 * Starts the service.
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		/*
		 * This is subject to change. The communication interface is about
		 * to be designed and implemented by Alex.
		 * 
		 * This here is only temporarily.
		 */
		
		DatagramSocket socket = new DatagramSocket(5555);
		
		while (run) {
			DatagramPacket packet = new DatagramPacket( new byte[256], 256);
			socket.receive(packet);
			numMessages += 1;
			logger.info("got {} messages so far", numMessages);
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
