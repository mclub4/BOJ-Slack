package com.example.commandtest.timer;

import com.example.commandtest.bojcrawl.BOJcrawl;
import com.example.commandtest.service.RecommendService;
import com.example.commandtest.slack.SlackSetMessage;
import com.example.commandtest.slack.SlacksendMessage;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 1분마다 반복해서 시간을 확인하고 시간이 되면 슬랙에 메시지를 보내는 Class
 */


@Slf4j
@EnableScheduling
@EnableAsync
@Configuration
public class Scheduler{

    @Autowired
    private BOJcrawl boJcrawl;

    @Autowired
    private RecommendService recommendService;

    private final GetDate getDate = new GetDate();
    private final SlacksendMessage slackSendMessage = new SlacksendMessage();
    private final SlackSetMessage slackSetMessage = new SlackSetMessage();

    private final RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
            .handle(ConnectException.class)
            .withDelay(Duration.ofSeconds(1))
            .withMaxRetries(10)
            .build();

    public Scheduler() throws IOException {
        getDate.setTime();
    }

    @Async
    @Scheduled(fixedRate = 60000)
    public void schedule(){
        if(getDate.checkDate()){
            try {
                int hour = LocalDateTime.now().getHour();
                int minute = LocalDateTime.now().getMinute();

                if(19<hour || (hour == 19 && minute>=30) || hour == 0){
                    StringBuilder sb = new StringBuilder();
                    sb.append("현재 시간 " + getDate.test() + "\n");
                    LocalDateTime time;
                    if(hour != 0){
                        time = getDate.START;
                    }
                    else if(hour == 0){
                        time = getDate.START;
                        getDate.setTime();
                    } else {
                        time = null;
                    }
                    ArrayList<String> tmp = Failsafe.with(retryPolicy).get(() -> boJcrawl.checkunSolvedUsers(time));

                    String message;
                    if(tmp.size()>0){
                        for(int i = 0; i<tmp.size(); i++){
                            sb.append("<@" + tmp.get(i) + "> " );
                        }
                        String mention = sb.toString() + "\n여러분들! ";
                        String timemessage = slackSetMessage.setMessage(hour, minute);
                        message = mention + timemessage;
                    }
                    else if(hour == 0){
                        message = "우와, 오늘은 전부다 문제를 푸셨군요! 정말 자랑스럽습니다!";
                    }
                    else{
                        message = "현재 시간 문제를 풀지 않은 사람이 없습니다.";
                    }
                    Failsafe.with(retryPolicy).run(() -> slackSendMessage.sendSlack(5, message));
                }
                if(hour == 12){
                    String recommend = recommendService.randomRepeat();
                    Failsafe.with(retryPolicy).run(() -> slackSendMessage.sendSlack(5, recommend));
                }
            } catch (Exception e) {
                log.info("Exception : {}", e);
                e.printStackTrace();
            }
        }
    }
}
