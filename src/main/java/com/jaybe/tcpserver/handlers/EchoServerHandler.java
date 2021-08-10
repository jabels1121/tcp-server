package com.jaybe.tcpserver.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    private final ChannelGroup group;

    public EchoServerHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Get the current successful connection with the server channel
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "---Go online");
        group.writeAndFlush(channel.remoteAddress() + "---Go online\n");
        // Will present channel Add to group in
        group.add(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        // Here we want to broadcast messages to all group Clients in Channel
        // The message sent to oneself is different from the message sent to everyone
        group.forEach(ch -> {
            ch.writeAndFlush(channel.remoteAddress() + ": " + msg);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
