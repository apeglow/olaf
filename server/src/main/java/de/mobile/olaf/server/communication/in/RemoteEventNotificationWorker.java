package de.mobile.olaf.server.communication.in;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mobile.common.domain.Ip4Address;
import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.api.Message;
import de.mobile.olaf.server.IpAddressUsageNotificationService;
import de.mobile.olaf.server.Olaf;

/**
 * Works the notifications coming from the partner platforms asynchronously.
 * 
 * @author andre
 * 
 *         This is only temporarily. The communication interface is to be
 *         designed by Alex.
 */
public class RemoteEventNotificationWorker implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final DatagramPacket datagramPacket;
	
	private final IpAddressUsageNotificationService ipAddressUsageNotificationService;

	public RemoteEventNotificationWorker(DatagramPacket datagramPacket,
			IpAddressUsageNotificationService ipAddressUsageNotificationService) {
		this.datagramPacket = datagramPacket;
		this.ipAddressUsageNotificationService = ipAddressUsageNotificationService;
	}

	@Override
	public void run() {
		byte[] data = Arrays.copyOfRange(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
		logger.debug("packet of size {} recieved {}", data.length, Arrays.toString(data));
		Message msg = Message.fromBytes(data);
		logger.info("msg {}", msg);
		ipAddressUsageNotificationService.notify(//
				new Ip4Address(msg.getIp()), //
				Olaf.ipAddress2SiteMap.get(msg.getClientId()),//
				IpUsedEventType.values()[msg.getEventId()]);
	}
}
