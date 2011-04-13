package de.mobile.olaf.client.intern;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UdpClient {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final DatagramSocket socket;
    private final InetSocketAddress address;
    private final ExecutorService executor;


    public UdpClient(String address, int port) {
        this(new InetSocketAddress(address, port),
            Executors.newSingleThreadExecutor());
    }

    public UdpClient(InetSocketAddress inetAddress, ExecutorService executor) {
        try {
        	this.address = inetAddress;
            this.socket = new DatagramSocket();
            //connect();
        } catch (SocketException e){
            throw new InstantiationError(e.getMessage());
        }
        this.executor = executor;
    }
    
    
    void connect() throws SocketException {
    	this.socket.connect(address);
    }

    public void sendMessage(final byte[] buf) {
//        if (!this.socket.isConnected()) {
//            try {
//            	connect();
//            } catch (SocketException e) {
//            	logger.severe("discard message");
//            	return;
//            }
//        }
        executor.execute(new Runnable() {
            @Override public void run() {
                try {
                	DatagramPacket p = new DatagramPacket(buf, buf.length, address);
                    socket.send(p);
                } catch (IOException e) {
                    logger.warning(e.getClass().getName());
                }
            }
        });
    }

    public void close() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            	System.out.println(executor.shutdownNow());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            socket.close();
        }
    }
    
}
