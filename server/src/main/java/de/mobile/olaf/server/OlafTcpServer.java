package de.mobile.olaf.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import de.mobile.common.domain.Ip4Address;
import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.api.Message;


public class OlafTcpServer {
    final ChannelFactory factory;
    final Channel channel;
    final IpAddressUsageNotificationService ipAddressUsageNotificationService;

    public OlafTcpServer(int port, IpAddressUsageNotificationService ipAddressUsageNotificationService) {
        this.factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
        ServerBootstrap b = new ServerBootstrap(factory);
        b.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        new MessageDecoder(),
                        new MessageHandler());
            }
        });
        this.channel = b.bind(new InetSocketAddress(port));
        this.ipAddressUsageNotificationService = ipAddressUsageNotificationService;
    }
    
    class MessageDecoder extends FrameDecoder {
        
        @Override
        protected Object decode(ChannelHandlerContext ctx, Channel channel,   ChannelBuffer buffer) throws Exception {
            if (buffer.readableBytes() < 20) {
                return null;
            }
            byte[] bytes = new byte[20];
            buffer.readBytes(bytes);
            return Message.fromBytes(bytes);
        }
    }
    
    class MessageHandler extends SimpleChannelHandler {
        
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
            Message msg = (Message) e.getMessage();
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
}
