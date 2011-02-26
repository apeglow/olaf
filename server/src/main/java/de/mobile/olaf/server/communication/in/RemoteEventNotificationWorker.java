package de.mobile.olaf.server.communication.in;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mobile.olaf.server.CommunicationException;
import de.mobile.olaf.server.IpAddressUsageNotificationService;
import de.mobile.olaf.server.Olaf;
import de.mobile.olaf.server.domain.IpUsageEventType;
import de.mobile.olaf.server.domain.PartnerSite;

/**
 * Works the notifications coming from the partner platforms 
 * asynchronously.
 * 
 * @author andre
 *
 */
public class RemoteEventNotificationWorker implements Runnable {
	private final Random siteDie = new Random();
	
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final DatagramPacket datagramPacket;
	private final IpAddressUsageNotificationService ipAddressUsageNotificationService;
	
	public RemoteEventNotificationWorker(DatagramPacket datagramPacket, IpAddressUsageNotificationService ipAddressUsageNotificationService){
		this.datagramPacket = datagramPacket;
		this.ipAddressUsageNotificationService = ipAddressUsageNotificationService;
	}
	
	@Override
	public void run() {
		InetAddress address = datagramPacket.getAddress();
//		Site site = Olaf.ipAddress2SiteMap.get(address.getHostAddress());
		
		PartnerSite site = Olaf.ipAddress2SiteMap.get(siteDie.nextInt(Olaf.ipAddress2SiteMap.size()));
		
		if (site != null){
			byte[] data = datagramPacket.getData();
			
			try {
				int mark = findMark(data);
				InetAddress usedAddress = InetAddress.getByAddress(Arrays.copyOf(data, mark));
				String strEventType = new String(Arrays.copyOfRange(data, mark+1, data.length)).trim();
				IpUsageEventType eventType = IpUsageEventType.valueOf(strEventType);
				ipAddressUsageNotificationService.notify(usedAddress, site, eventType);
				
			} catch (CommunicationException e){
				logger.info("Received invalid request.");
			} catch (IOException e){
				logger.info("Received info for an invalid ip-address.");
			}
		} else {
			logger.warn("Unregistered site "+address+" sent notification.");
		}
	}
	
	private int findMark(byte[] array) throws CommunicationException{
		for (int pos=0; pos<array.length; pos++){
			if (array[pos] == 0){
				return pos;
			}
		}
		
		throw new CommunicationException("No mark found.");
		
	}
	
	
}
