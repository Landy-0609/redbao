package com.xzl.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedPacketProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public RedPacketProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaHandler
    public void sendRedPacketClaimedMessage(String topic, Long redPacketId, Long userId) {
        String message = "用户 " + userId + " 领取了 " + redPacketId;
        kafkaTemplate.send(topic, message);
    }
}
