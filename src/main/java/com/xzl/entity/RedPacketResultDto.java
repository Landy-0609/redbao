package com.xzl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Builder
@NoArgsConstructor
@Data
public class RedPacketResultDto {

    private Long userId;
    private String redPacketId;
    private BigDecimal amountGrabbed;
    private Boolean success;
    private String message;

    public RedPacketResultDto(Long userId, String redPacketId, BigDecimal amountGrabbed, Boolean success, String message) {
        this.userId = userId;
        this.redPacketId = redPacketId;
        this.amountGrabbed = amountGrabbed;
        this.success = success;
        this.message = message;
    }

    public RedPacketResultDto(Long userId, String redPacketId, BigDecimal amountGrabbed, boolean success) {
        this.userId = userId;
        this.redPacketId = redPacketId;
        this.amountGrabbed = amountGrabbed;
        this.success = success;
        this.message = success ? "抢红包成功" : "抢红包失败";
    }
}
