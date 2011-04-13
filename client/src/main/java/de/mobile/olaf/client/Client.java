package de.mobile.olaf.client;

import java.io.Closeable;

import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.api.Message;
import de.mobile.olaf.client.intern.NettyUdpClient;
import de.mobile.olaf.client.util.HexUtils;

public class Client implements Closeable {
	
	final NettyUdpClient endpoint = new NettyUdpClient("localhost", 5555);
	
	final int clientId = 1;
	
	public void sendMessage(String ip, IpUsedEventType type) {
		Message msg = new Message.Builder()
			.setIp(ip)
			.setEventId(type.ordinal())
			.setClientId(clientId)
			.setTime(System.currentTimeMillis())
			.build();
		endpoint.sendMessage(HexUtils.toHexString(msg.asBytes()).getBytes());
	}
	
	public void close() {
		endpoint.close();
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		for (int i = 0; i < 1000; i++)
			client.sendMessage("192.168.2.1", IpUsedEventType.USE);
		client.close();
	}

}
