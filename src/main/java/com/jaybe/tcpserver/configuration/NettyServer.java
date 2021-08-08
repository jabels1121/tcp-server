package com.jaybe.tcpserver.configuration;

import com.jaybe.tcpserver.handlers.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;


@Component
@Slf4j
public class NettyServer {

    private final Integer port;
    /**
     * boss Thread groups are used to handle connection work
     */
    private final EventLoopGroup boss = new NioEventLoopGroup(1);
    /**
     * work Thread groups for data processing
     */
    private final EventLoopGroup work = new NioEventLoopGroup();

    public NettyServer(@Value("${tcp.server.port}") Integer port) {
        this.port = port;
    }

    @PostConstruct
    public void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class) // (3)
                //Set socket address using specified port
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new EchoServerHandler());//discardServerHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 1024)          // (5)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);

        ChannelFuture future = serverBootstrap.bind().sync();
        if (future.isSuccess()) {
            log.info("start-up Netty Server");
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        work.shutdownGracefully().sync();
        boss.shutdownGracefully().sync();
        log.info("Close Netty");
    }

}
