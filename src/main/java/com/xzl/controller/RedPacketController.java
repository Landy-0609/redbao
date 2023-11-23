package com.xzl.controller;

import com.xzl.entity.RedPacketDto;
import com.xzl.entity.RedPacketResultDto;
import com.xzl.service.RedPacketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/red-packets")
public class RedPacketController {

    private final RedPacketService redPacketService;

    // Constructor injection is recommended
    public RedPacketController(RedPacketService redPacketService) {
        this.redPacketService = redPacketService;
    }

    @PostMapping("/simulate")
    public void simulateGrabbingRedPacket(String redPacketId, int numberOfUsers){
        redPacketService.simulateGrabbingRedPacket(redPacketId,numberOfUsers);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createRedPacket(RedPacketDto redPacketDto) {
        String redPacketId =  redPacketService.createRedPacket(redPacketDto);
        return ResponseEntity.ok("红包发出去了！ + 红包编码：  " + redPacketId);
    }

    @PostMapping("/grab/{redPacketId}")
    public ResponseEntity<?> grabRedPacket( String redPacketId, Long userId) {
        RedPacketResultDto result = redPacketService.grabRedPacket(redPacketId, userId);
        return ResponseEntity.ok(result);
    }
}


