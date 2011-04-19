package de.mobile.olaf.client.intern;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

public class NettyTcpClient implements Closeable {

    final ChannelFactory factory;
    final InetSocketAddress address;
    final Timer timer = new HashedWheelTimer();
    final UptimeClientHandler uptimeClientHandler;

    public NettyTcpClient(String host, int port) {
        address = new InetSocketAddress(host, port);
        factory = new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()
        );
        final ClientBootstrap bootstrap = new ClientBootstrap(factory);
        uptimeClientHandler = new UptimeClientHandler(bootstrap, address, timer); 
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline( //
                        new ByteBufferEncoder(), //
                        uptimeClientHandler);
            }
        });
        bootstrap.connect(address);
//        channel.awaitUninterruptibly();
//        if (!channel.isSuccess()) {
//            channel.getCause().printStackTrace();
//        }
    }

    public void sendMessage(final byte[] buf) {
        //System.out.println(HexUtils.toHexString(buf));
        Channel channel = uptimeClientHandler.getChannel();
        if (channel != null)
            channel.write(buf, address);
    }

    public void close() {
        Channel channel = uptimeClientHandler.getChannel();
        if (channel != null)
            channel.close().awaitUninterruptibly();
        factory.releaseExternalResources();
    }

    static class ByteBufferEncoder extends OneToOneEncoder {
        protected Object encode(ChannelHandlerContext context, Channel channel,
                Object obj) throws Exception {
            return ChannelBuffers.wrappedBuffer((byte[]) obj);
        }
    }
}
