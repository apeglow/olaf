package de.mobile.olaf.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	
	public static void main(String[] args) throws IOException {
		String server = args[0]; 
		String ipAddress = args[1];
		String eventType = args[2];
		
		DatagramSocket socket = new DatagramSocket(); 
		InetAddress ia =  InetAddress.getByName(server);		
		InetAddress notifyTarget = InetAddress.getByName(ipAddress);
		
		
		byte[] notifyTargetAsArray = notifyTarget.getAddress();
		byte[] eventTypeAsArray = eventType.getBytes();
		
		byte[] msgBytes = new byte[notifyTargetAsArray.length+1+eventTypeAsArray.length];
		for (int pos=0; pos<notifyTargetAsArray.length; pos++){
			msgBytes[pos] = notifyTargetAsArray[pos];
		}
		msgBytes[notifyTargetAsArray.length] = 0;
		
		for (int pos=0; pos<eventTypeAsArray.length; pos++){
			msgBytes[notifyTargetAsArray.length+pos+1] = eventTypeAsArray[pos];
		}
		
		DatagramPacket packet = new DatagramPacket( msgBytes, msgBytes.length, ia, 5555);
		socket.send( packet ); 
	}

}
