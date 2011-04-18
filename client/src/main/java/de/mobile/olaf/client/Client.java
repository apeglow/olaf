package de.mobile.olaf.client;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.api.Message;
import de.mobile.olaf.client.intern.NettyUdpClient;

public class Client implements Closeable {

    final NettyUdpClient endpoint;

    final int clientId;

    public Client(String host, int port, int clientId) {
        this.endpoint = new NettyUdpClient(host, port);
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
    
    public void sendMessage(int ip, IpUsedEventType type) {
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

    public static void main(String[] args) throws IOException {
        URI uri = URI.create(args.length < 1 ? "//1@localhost:5555" : args[0]);
        int clientId = uri.getUserInfo() == null ? 1 : Integer.parseInt(uri
                .getUserInfo());

        Client client = new Client(uri.getHost(), uri.getPort(), clientId);
        try {
            BufferedReader reader = new BufferedReader(
                (new InputStreamReader(System.in)));
            String line;
        
            while ((line = reader.readLine()) != null) {
                client.sendMessage(line, IpUsedEventType.USE);
            }
        } finally {
            client.close();
        }
//        for (long l = 0; l <= 0xffffffL; l++) {
//            client.sendMessage((int)l , IpUsedEventType.USE);
//        }
//        client.close();
    }
}
