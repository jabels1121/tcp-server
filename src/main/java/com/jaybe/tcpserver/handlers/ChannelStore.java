package com.jaybe.tcpserver.handlers;

import io.netty.channel.Channel;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelStore {

    @Getter
    private final ConcurrentHashMap<String, Channel> store = new ConcurrentHashMap<>();

    public void add(String id, Channel channel) {
        store.putIfAbsent(id, channel);
    }

    public void delete(String id) {
        store.remove(id);
    }

    public Channel get(String id) {
        return store.get(id);
    }

}
