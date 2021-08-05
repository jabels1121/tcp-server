package com.jaybe.tcpserver.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ChannelHandler.Sharable
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    private final String messagePattern = "Hello %s from server!";
    private final ChannelStore channelStore;
    private final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public DiscardServerHandler(ChannelStore channelContextStore) {
        this.channelStore = channelContextStore;
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
                ch.writeAndFlush(channel.remoteAddress() + ": " + msg + "\r\n");
        });
        group.write(msg);
//        ctx.channel().writeAndFlush(msg);
    }

    private void doSmth(ChannelHandlerContext ctx, Object msg) {
        var in = (ByteBuf) msg;
        var strMsg = in.toString(CharsetUtil.US_ASCII);
        if (strMsg.contains("name:")) {
            var split = strMsg.split(":");
            var name = split[1];
            channelStore.add(name, ctx.channel());
        }
        log.info(strMsg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
