package de.mobile.olaf.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import de.mobile.common.domain.Ip4Address;
import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.api.Message;


public class OlafUdpServer {
    final ChannelFactory factory;
    final Channel channel;
    final IpAddressUsageNotificationService ipAddressUsageNotificationService;

    public OlafUdpServer(int port, IpAddressUsageNotificationService ipAddressUsageNotificationService) {
        this.factory = new NioDatagramChannelFactory(Executors.newCachedThreadPool());
        ConnectionlessBootstrap b = new ConnectionlessBootstrap(factory);
        b.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        new ByteBufferDecoder(),
                        new ServerHandler());
            }
        });
        this.channel = (DatagramChannel) b.bind(new InetSocketAddress(port));
        this.ipAddressUsageNotificationService = ipAddressUsageNotificationService;
    }
    
    class ServerHandler extends SimpleChannelUpstreamHandler {
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            Message msg = Message.fromBytes((byte[]) e.getMessage());
            ipAddressUsageNotificationService.notify(
                    new Ip4Address(msg.getIp()),
                    Olaf.ipAddress2SiteMap.get(msg.getClientId()),//
                    IpUsedEventType.values()[msg.getEventId()]);
        }
    }
    
    public void close() {
        channel.close().awaitUninterruptibly(30, TimeUnit.SECONDS);
        factory.releaseExternalResources();
    }
    
    static class ByteBufferDecoder extends OneToOneDecoder {
        @Override
        protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
            if (!(msg instanceof ChannelBuffer)) {
                return msg;
            }
            return((ChannelBuffer) msg).array();
        }
    }
    
}
