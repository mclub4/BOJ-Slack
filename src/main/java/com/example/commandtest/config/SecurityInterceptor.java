package com.example.commandtest.config;

import com.example.commandtest.service.SlackSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Request가 슬랙에서 보낸 유효한 Request인지 확인하는 기능
 */

@Slf4j
@Configuration
public class SecurityInterceptor implements HandlerInterceptor {

    private static final String REQUEST_TIMESTAMP = "X-Slack-Request-Timestamp";
    private static final String SLACK_SIGNATURE = "X-Slack-Signature";

    private final SlackSecurityService slackSecurityService;

    public SecurityInterceptor(SlackSecurityService slackSecurityService) {
        this.slackSecurityService = slackSecurityService;
    }

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        try {
            String requestBody = parameterMapToString(request.getParameterMap());
            log.info("[SecurityInterceptor.preHandle] request body to String: {}", requestBody);

            Long requestTimeStamp = Long.parseLong(request.getHeader(REQUEST_TIMESTAMP));
            return slackSecurityService.isValidByRequestDate(requestTimeStamp) &&
                    slackSecurityService.isValidRequestByHmac(request.getHeader(SLACK_SIGNATURE), requestTimeStamp, requestBody);
        } catch (Exception e) {
            log.error("[SecurityInterceptor.preHandle] error for request", e);
            throw new Exception("비정상적인 요청입니다.");
        }
    }

    private String parameterMapToString(Map<String, String[]> requestParameterMap) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;

        for (Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
            String value = entry.getValue()[0];

            if (entry.getKey().equals("response_url") || entry.getKey().equals("command") || entry.getKey().equals("text")) {
                value = URLEncoder.encode(value, "UTF-8");
            }

            stringBuilder
                    .append(entry.getKey())
                    .append("=")
                    .append(value);

            count += 1;
            if (count != requestParameterMap.size()) {
                stringBuilder.append("&");
            }
        }

        return stringBuilder.toString();
    }
}