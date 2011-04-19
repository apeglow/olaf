package de.mobile.olaf.client;

import java.io.Closeable;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.api.Message;
import de.mobile.olaf.client.intern.NettyTcpClient;

public class Client implements Closeable {

    final NettyTcpClient endpoint;

    final int clientId;

    public Client(String host, int port, int clientId) {
        this.endpoint = new NettyTcpClient(host, port);
        this.clientId = clientId;
    }

    public void sendMessage(String ip, IpUsedEventType type) {
        Message msg = new Message.Builder() //
            .setIp(ip) //
            .setEventId(type.ordinal()) //
            .setClientId(clientId) //
            .setTime(System.currentTimeMillis()).build();
        endpoint.sendMessage(msg.asBytes());
    }
    
    public void close() {
        endpoint.close();
    }

    public static void main(String[] args) throws Exception {
        URI uri = URI.create(args.length < 1 
                ? "tcp://0@localhost:5555" 
                : args[0]);
        int clientId = uri.getUserInfo() == null 
            ? 1 
            : Integer.parseInt(uri.getUserInfo());
        Client client = new Client(uri.getHost(), uri.getPort(), clientId);
        client.sendMessage("127.0.0.1", IpUsedEventType.USE);
        TimeUnit.MILLISECONDS.sleep(10);
        //client.close();
    }
}
