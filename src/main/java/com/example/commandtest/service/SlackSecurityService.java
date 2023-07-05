package com.example.commandtest.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Request가 슬랙에서 보낸 유효한 Request인지 확인하는 기능
 */

@Slf4j
@Service
public class SlackSecurityService {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final Mac macSha256;

    public SlackSecurityService(Mac macSha256) {
        this.macSha256 = macSha256;
    }

    public boolean isValidByRequestDate(Long timeStamp) {
        Date requestDate = Date.from(Instant.ofEpochSecond(timeStamp));
        LocalDateTime requestLocalDateTime = LocalDateTime.ofInstant(requestDate.toInstant(), ZoneId.systemDefault());
        return !requestLocalDateTime.isBefore(LocalDateTime.now().minusMinutes(5));
    }

    public boolean isValidRequestByHmac(String slackSignature, Long timeStamp, String requestBody) {
        String baseString = "v0:" + timeStamp + ":" + requestBody;
        return slackSignature.equals("v0=" + encodeBaseStringByHmac256(baseString));
    }

    private String encodeBaseStringByHmac256(String baseString) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec("slack app 의 Signing Secret key를 넣도록 함".getBytes(), HMAC_SHA256);
            macSha256.init(secretKeySpec);
            byte[] bytes = macSha256.doFinal(baseString.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(bytes);
        } catch (InvalidKeyException e) {
            log.error("[SlackSecurityService.encodeBaseStringByHmac256] encoding error", e);
        }

        return "";
    }

}