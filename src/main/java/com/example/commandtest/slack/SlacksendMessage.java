package com.example.commandtest.slack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;

/**
 * 슬랙에 메시지 보내기
 */

@Slf4j
public class SlacksendMessage extends SlackInfo{
    public SlacksendMessage() throws IOException {
        super();
    }

    // 원하는 채널을 선택하여 메시지를 보내고 싶을 때
    public void sendSlack(int channelnum, String text) throws Exception{
        try{
            //Get OAuth token&channelID
            SlackInfo slackInfo = new SlackInfo();
            final String token = slackInfo.getToken();

            /** Slack 팀마다 다르니 알아서 설정 할 것
             * 채널번호 0 : 1일1백준, 채널번호 1 : 공지사항, 채널번호 2 : 테스트
             * 채널번호 3 : 공지사항, 채널번호 4 : 일반, 채널번호 5 : 백준 알림
             */
            final String channelID = slackInfo.getChannelId(channelnum);

            //Slack Bot Post URL Setting&Link
            String postURL = "https://slack.com";

            WebClient webClient = WebClient.builder()
                    .baseUrl(postURL)
                    .build();

            MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
            bodyMap.add("channel", channelID);
            bodyMap.add("text", text);
            bodyMap.add("unfurl_media", "false");
            bodyMap.add("pretty", "1");

            webClient.post()
                    .uri("/api/chat.postMessage")
                    .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+ token)
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .body(BodyInserters.fromFormData(bodyMap))
                    .retrieve()
                    .bodyToFlux(Void.class)
                    .blockLast();

        }catch (Exception e){
            log.info("Exception : {}", e);
            e.printStackTrace();
        }
    }
}
