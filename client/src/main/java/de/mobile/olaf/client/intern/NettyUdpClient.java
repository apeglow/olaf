package de.mobile.olaf.client.intern;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.jboss.netty.channel.ExceptionEvent;

public class NettyUdpClient implements Closeable {

    final DatagramChannelFactory factory;
    final DatagramChannel channel;
    final InetSocketAddress address;

    public NettyUdpClient(String host, int port) {
        address = new InetSocketAddress(host, port);
        factory = new NioDatagramChannelFactory(Executors.newCachedThreadPool());
        ConnectionlessBootstrap b = new ConnectionlessBootstrap(factory);
        b.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new ByteBufferEncoder(),
                        new SimpleChannelUpstreamHandler() {
                            public void exceptionCaught(
                                    ChannelHandlerContext ctx, ExceptionEvent e)
                                    throws Exception {
                                Logger.getLogger(getClass().getName()).info(
                                        e.toString());
                            };
                        });
            }
        });
        channel = (DatagramChannel) b.bind(new InetSocketAddress(0));
    }

    public void sendMessage(final byte[] buf) {
        channel.write(buf, address);
    }

    public void close() {
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
