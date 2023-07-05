package com.example.commandtest.slack;


import com.example.commandtest.config.EnvManager;

import java.io.IOException;

/**
 * env.properties로 부터 token, channelID, teamID 받아오기
 */

public class SlackInfo {
    private String token;
    private String channelId;
    private String teamId;

    /** Slack 팀마다 다르니 알아서 설정 할 것
    * 채널번호 0 : 1일1백준, 채널번호 1 : 공지사항, 채널번호 2 : 테스트
    * 채널번호 3 : 공지사항, 채널번호 4 : 일반, 채널번호 5 : 백준 알림
    */
    private final String[] channelAttribution = {"OneDay", "Information", "Test", "Broadcast", "Basic", "Alarm"};
    private EnvManager envManager;

    public SlackInfo() throws IOException {
        this.envManager = new EnvManager();
    }

    public String getToken(){
        this.token = envManager.getValueByKey("token");
        return token;
    }

    public String getChannelId(int channelnum){
        String channelName = "channel" + channelAttribution[channelnum];
        this.channelId = envManager.getValueByKey(channelName);
        return channelId;
    }

    public String getTeamId(){
        this.teamId = envManager.getValueByKey("teamID");
        return teamId;
    }
}