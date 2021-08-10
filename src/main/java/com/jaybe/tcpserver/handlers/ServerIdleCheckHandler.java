package com.jaybe.tcpserver.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ServerIdleCheckHandler extends IdleStateHandler {

    public ServerIdleCheckHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        super(readerIdleTime, writerIdleTime, allIdleTime, unit);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            Channel channel = ctx.channel();
            if (e.state() == IdleState.READER_IDLE) {
                channel.writeAndFlush(channel.remoteAddress() + ": " + "read idle");
            } else if (e.state() == IdleState.WRITER_IDLE) {
                channel.writeAndFlush(channel.remoteAddress() + ": " + "write idle");
            }
        }
    }
}
