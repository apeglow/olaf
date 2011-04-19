package de.mobile.olaf.client.intern;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

public class UptimeClientHandler extends SimpleChannelHandler {

    /* Reconnect delay in seconds. */
    private static final long RECONNECT_DELAY = 5;
    private final ClientBootstrap bootstrap;
    private final Timer timer;
    private final InetSocketAddress address;
    private long startTime = -1;
    private Channel channel;
    

    public UptimeClientHandler(ClientBootstrap bootstrap, InetSocketAddress address, Timer timer) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.address = address;
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        println("Disconnected");
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
        println("Sleeping for: " + RECONNECT_DELAY + "s");
        timer.newTimeout(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                println("Reconnecting");
                setChannel(bootstrap.connect(address).getChannel());
            }
        }, RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }
        setChannel(ctx.getChannel());
        println("Connected");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        Throwable cause = e.getCause();
        if (cause instanceof ConnectException) {
            startTime = -1;
            println("Failed to connect: " + cause.getMessage());
        }
        ctx.getChannel().close();
        setChannel(null);
    }

    void println(String msg) {
        if (startTime < 0) {
            System.err.format("[SERVER IS DOWN] %s%n", msg);
        } else {
            System.err.format("[UPTIME: %5ds] %s%n", (System.currentTimeMillis() - startTime) / 1000, msg);
        }
    }
    
    private synchronized void setChannel(Channel channel) {
        this.channel = channel;
    }
    
    public synchronized Channel getChannel() {
        return channel;
    }

}
