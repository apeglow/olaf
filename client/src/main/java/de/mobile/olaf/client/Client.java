package de.mobile.olaf.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	
	public static void main(String[] args) throws IOException {
		String server = args[0]; 
		String msg = args[1];
		
		DatagramSocket socket = new DatagramSocket(); 
		InetAddress ia =  InetAddress.getByName(server);
		byte[] msgBytes = msg.getBytes();
		DatagramPacket packet = new DatagramPacket( msgBytes, msgBytes.length, ia, 5555);
		socket.send( packet ); 
	}

}
