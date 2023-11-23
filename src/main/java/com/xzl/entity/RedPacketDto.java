package com.xzl.entity;

import lombok.*;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.Name;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConstructorBinding
public class RedPacketDto {
    private String senderId;
    private BigDecimal remainingAmount;
    private Integer remainingCount;



}
