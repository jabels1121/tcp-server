package com.jaybe.tcpserver.configuration;

import com.jaybe.tcpserver.handlers.ChannelStore;
import com.jaybe.tcpserver.handlers.DiscardServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class TcpServer implements DisposableBean {

    private final Integer port;
    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public TcpServer(@Value("${tcp.server.port}") Integer port,
                     DiscardServerHandler discardServerHandler,
                     ChannelStore channelStore) throws InterruptedException {
        this.port = port;
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        config(discardServerHandler, channelStore);
        run();
    }

    public void run() throws InterruptedException {
        // Bind and start to accept incoming connections.
        ChannelFuture f = serverBootstrap.bind(port).sync(); // (7)

        // Wait until the server socket is closed.
        // In this example, this does not happen, but you can do that to gracefully
        // shut down your server.
        f.channel().closeFuture().sync();
    }

    @Override
    public void destroy() throws Exception {
        workerGroup.shutdownGracefully().wait();
        bossGroup.shutdownGracefully().wait();
    }

    public void config(DiscardServerHandler discardServerHandler, ChannelStore channelStore) {
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) // (3)
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(discardServerHandler);//discardServerHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
    }


}
