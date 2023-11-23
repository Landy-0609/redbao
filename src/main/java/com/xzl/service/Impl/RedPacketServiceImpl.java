package com.xzl.service.Impl;

import com.xzl.entity.RedPacketDto;
import com.xzl.entity.RedPacketResultDto;
import com.xzl.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RedPacketServiceImpl implements RedPacketService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private  RedisTemplate redisTemplate;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;


    @Override
    public void simulateGrabbingRedPacket(String redPacketId, int numberOfUsers) {
        // 用于跟踪成功抢到红包的用户数量
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 1; i <= numberOfUsers; i++) {
            Long userId = (long) i;

            taskExecutor.execute(() -> {
                RedPacketResultDto result = grabRedPacket(redPacketId, userId);
                String topic = "redbao";
                String message;
                if (result.getSuccess()) {
                    // 增加成功抢红包的用户数量
                    int count = successCount.incrementAndGet();
                    message = "用户id： " + userId + "抢到了红包，金额为" + result.getAmountGrabbed();
                    kafkaTemplate.send(topic, message);
                    System.out.println("总共抢到红包的人数: " + count);
                } else {
                    message = "用户id " + userId + "没有抢到红包，原因是: " + result.getMessage();
                    kafkaTemplate.send(topic, message);
                }
            });
        }
    }


    @Override
    public String createRedPacket(RedPacketDto redPacketDto) {
        String redPacketId = UUID.randomUUID().toString();
        kafkaTemplate.send("redbao", "用户编号:" + redPacketDto.getSenderId() + ",发送了一个红包,快来抢吧" );
        redisTemplate.opsForValue().set(redPacketId,redPacketDto);
        return redPacketId;
    }

    @Override
    public RedPacketResultDto grabRedPacket(String redPacketId, Long userId) {
        String lockKey = "lock:" + redPacketId;
        String grabbedUsersKey = "grabbedUsers:" + redPacketId;
        boolean lockAcquired = false;
        int retryCount = 10;

        try {
            while (!lockAcquired && retryCount > 0) {
                lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, userId.toString(), 1, TimeUnit.SECONDS);
                if (!lockAcquired) {
                    Thread.sleep(100);
                    retryCount--;
                }
            }

            if (!lockAcquired) {
                return new RedPacketResultDto(userId, redPacketId, BigDecimal.ZERO, false, "系统繁忙，请稍后再试");
            }

            Boolean hasGrabbed = redisTemplate.opsForSet().isMember(grabbedUsersKey, userId.toString());
            if (hasGrabbed != null && hasGrabbed) {
                return new RedPacketResultDto(userId, redPacketId, BigDecimal.ZERO, false, "您已经抢过该红包，不能再次抢取");
            }

            RedPacketDto redPacketDto = (RedPacketDto) redisTemplate.opsForValue().get(redPacketId.toString());

            if (redPacketDto == null) {
                return new RedPacketResultDto(userId, redPacketId, BigDecimal.ZERO, false, "红包不存在");
            }

            if (redPacketDto.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0 || redPacketDto.getRemainingCount() <= 0) {
                redisTemplate.delete(lockKey);
                return new RedPacketResultDto(userId, redPacketId, BigDecimal.ZERO, false, "手慢了，红包派完了");
            }

            BigDecimal amountToGrab = calculateRedPacketAmount(redPacketDto);
            redPacketDto.setRemainingAmount(redPacketDto.getRemainingAmount().subtract(amountToGrab));
            redPacketDto.setRemainingCount(redPacketDto.getRemainingCount() - 1);

            redisTemplate.opsForValue().set(redPacketId.toString(), redPacketDto);
            redisTemplate.opsForSet().add(grabbedUsersKey, userId.toString());
            redisTemplate.delete(lockKey);

            return new RedPacketResultDto(userId, redPacketId, amountToGrab, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal calculateRedPacketAmount(RedPacketDto redPacketDto) {
        if (redPacketDto.getRemainingCount() == 1) {
            return redPacketDto.getRemainingAmount();
        } else {
            Random random = new Random();
            double randomValue = random.nextDouble() * redPacketDto.getRemainingAmount().doubleValue();
            double truncatedValue = Math.floor(randomValue * 100) / 100;
            BigDecimal amount = BigDecimal.valueOf(truncatedValue);
            if (amount.compareTo(BigDecimal.valueOf(0.01)) < 0) {
                amount = BigDecimal.valueOf(0.01);
            }

            return amount;
        }
    }


}



