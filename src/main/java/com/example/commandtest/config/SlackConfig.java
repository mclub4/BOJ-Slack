package com.example.commandtest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;

/**
 * Request가 슬랙에서 보낸 유효한 Request인지 확인하는 기능
 */

@Configuration
public class SlackConfig {
    @Bean
    public Mac createMacSha256() throws NoSuchAlgorithmException {
        return Mac.getInstance("HmacSHA256");
    }
}
