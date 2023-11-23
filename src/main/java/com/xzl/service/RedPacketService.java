package com.xzl.service;

import com.xzl.entity.RedPacketDto;
import com.xzl.entity.RedPacketResultDto;

public interface RedPacketService {

    void simulateGrabbingRedPacket(String redPacketId, int numberOfUsers);

    String createRedPacket(RedPacketDto redPacketDto);


    RedPacketResultDto grabRedPacket(String redPacketId, Long userId);
}
