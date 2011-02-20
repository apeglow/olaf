package de.mobile.olaf.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.mobile.olaf.server.domain.Country;
import de.mobile.olaf.server.domain.Site;
import de.mobile.olaf.server.domain.SiteType;

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
	public final static Map<String, Site> ipAddress2SiteMap = new HashMap<String, Site>();
	static {
		ipAddress2SiteMap.put("127.0.0.1", new Site("test", Country.DE, SiteType.GENERAL_CLASSIFIED));
	}
	
	private final Log logger = LogFactory.getLog(getClass());
	private final IpAddressUsageNotificationService ipAddressUsageNotificationService;
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean run = true;
	
	
	
	public static void main(String[] args) throws IOException {
		Olaf olaf = new Olaf();
		olaf.start();
	}
	
	
	
	public Olaf(){
		ipAddressUsageNotificationService = new IpAddressUsageNotificationService();
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
			
			NotificationWorker notificationWorker = new NotificationWorker(packet, ipAddressUsageNotificationService);
			executorService.execute(notificationWorker);
		}
		
	}
	
	
	public void stop(){
		run = false;
		executorService.shutdown();
	}
	

	

}
