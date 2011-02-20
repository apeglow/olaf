package de.mobile.olaf.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.mobile.olaf.server.domain.IpUsageEventType;
import de.mobile.olaf.server.domain.Site;

/**
 * Works the notification asynchronously.
 * 
 * @author andre
 *
 */
public class NotificationWorker implements Runnable {
	private final Log logger = LogFactory.getLog(getClass());
	private final DatagramPacket datagramPacket;
	private final IpAddressUsageNotificationService ipAddressUsageNotificationService;
	
	public NotificationWorker(DatagramPacket datagramPacket, IpAddressUsageNotificationService ipAddressUsageNotificationService){
		this.datagramPacket = datagramPacket;
		this.ipAddressUsageNotificationService = ipAddressUsageNotificationService;
	}
	
	@Override
	public void run() {
		InetAddress address = datagramPacket.getAddress();
		Site site = Olaf.ipAddress2SiteMap.get(address.getHostAddress());
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
