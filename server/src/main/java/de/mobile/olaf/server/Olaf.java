package de.mobile.olaf.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Olaf {
	
	public static void main(String[] args) throws IOException {
		DatagramPacket packet = new DatagramPacket( new byte[256], 256);
		
		DatagramSocket socket = new DatagramSocket(5555);
		
		
		while(true){
			socket.receive(packet);
			String text = new String(packet.getData(), 0, packet.getLength());
			System.out.println(text);

		}
		

		
	}

}
