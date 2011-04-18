package de.mobile.olaf.client.intern;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class NettyTcpClient implements Closeable {

    final ChannelFactory factory;
    final ChannelFuture channel;
    final InetSocketAddress address;

    public NettyTcpClient(String host, int port) {
        address = new InetSocketAddress(host, port);
        factory = new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()
        );
        ClientBootstrap b = new ClientBootstrap(factory);
        
        b.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new ByteBufferEncoder(),
                        new SimpleChannelUpstreamHandler() {
                            public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
                                Logger.getLogger(getClass().getName()).info(e.toString());
                            };
                        });
            }
        });
        channel = b.connect(address);
//        channel.awaitUninterruptibly();
//        if (!channel.isSuccess()) {
//            channel.getCause().printStackTrace();
//        }
    }

    public void sendMessage(final byte[] buf) {
        //System.out.println(HexUtils.toHexString(buf));
        channel.getChannel().write(buf, address);
    }

    public void close() {
        channel.getChannel().close().awaitUninterruptibly();
        factory.releaseExternalResources();
    }

    static class ByteBufferEncoder extends OneToOneEncoder {
        protected Object encode(ChannelHandlerContext context, Channel channel,
                Object obj) throws Exception {
            return ChannelBuffers.wrappedBuffer((byte[]) obj);
        }
    }
}
