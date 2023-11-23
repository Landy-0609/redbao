package com.xzl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Slf4j
@SpringBootApplication
@EnableWebMvc
public class RedPacketApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedPacketApplication.class, args);
    }

}
