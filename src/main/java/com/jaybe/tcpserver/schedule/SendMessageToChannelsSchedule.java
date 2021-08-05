package com.jaybe.tcpserver.schedule;

import com.jaybe.tcpserver.handlers.ChannelStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendMessageToChannelsSchedule {

    private final ChannelStore channelStore;
    private final String messagePattern = "Hello %s from server!";

    public SendMessageToChannelsSchedule(ChannelStore channelStore) {
        this.channelStore = channelStore;
    }

    @Scheduled(initialDelay = 5000L, fixedDelay = 5000L)
    public void sendMessages() {
        log.info("Start sending messages.");
        channelStore.getStore()
                .forEach((s, channel) -> {

                    if (channel.isActive()) {
                        channel.writeAndFlush(String.format(messagePattern, s));
                    } else {
                        log.warn("Channel with name=[{}] is not active!", s);
                    }
                });
    }

}
