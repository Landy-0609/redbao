package com.xzl.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RedPacketConsumer {

    @KafkaListener(topics = "redbao")
    public void listen(String message) {
        System.out.println("来自kafka的消息~~~" + message);
    }
}
