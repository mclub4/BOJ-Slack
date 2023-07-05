package com.example.commandtest.bojcrawl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;

@Slf4j
@Getter
@Setter
@Component
public class SolvedAC{
    //SolvedAC api를 이용하여 JSON 파싱 후, 문제의 알고리즘 분류 및 난이도를 가져오는 기능
    public JSONArray response;
    private JSONObject curObject;
    private JSONArray tagArray;
    private String levelMessage = "";
    private final String[] getTier = {":b", ":s", ":g", ":p", ":d", ":r"};

    // 문제 정보 받아오기
    public void getProblem(String num){
        try {
            String getURL = "https://solved.ac/api/v3/problem/lookup?problemIds=" + num;

            String jsonstring = WebClient.create()
                    .get()
                    .uri(getURL)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            response = new JSONArray(jsonstring);
        }
        catch(NullPointerException e){
            log.info("Exception : {" + e + "}");
            levelMessage += "문제 번호가 잘못되었습니다. 올바른 값을 입력해주세요.";
        }
        catch(WebClientResponseException.BadRequest e){
            log.info("Exception : {" + e + "}");
            levelMessage += "문제 번호가 잘못되었습니다. 올바른 값을 입력해주세요.";
        }
        catch(WebClientRequestException e){
            log.info("Exception : {" + e + "}");
            levelMessage = "서버에 문제가 생겨 정보를 받아올 수 없습니다.";
        }
        catch (Exception e){
            log.info("Exception : {" + e + "}");
            levelMessage = "예상치 못한 이유로 정보를 받아올 수 없습니다.";
        }
    }

    //유저 정보 받아오기
    public String getUser(String ID){
        String userMessage = "";

        try {
            String getURL = "https://solved.ac/api/v3/user/show?handle=" + ID;

            String jsonstring = WebClient.create()
                    .get()
                    .uri(getURL)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject user = new JSONObject(jsonstring);

            if(user != null){
                int level = (Integer)user.get("tier");
                int tier;
                if(level != 0){
                    int tierlevel = level%5 != 0 ? level/5:level/5-1;
                    userMessage += getTier[tierlevel];
                    tier = level%5;
                    if(tier == 0) userMessage += "1: ";
                    else userMessage += (5-tier+1) + ": ";
                }
                else{
                    userMessage += "Unrated";
                }

                userMessage += ID;
                String bios = (String)user.get("bio");
                userMessage += "\n소개 : " + bios;
                int classGet = (Integer)user.get("class");
                userMessage += "\n클래스 : " + classGet;
                int solvedproblems = (Integer)user.get("solvedCount");
                userMessage += "\n푼 문제 수 : " + solvedproblems;
                int maxStreak = (Integer)user.get("maxStreak");
                userMessage += "\n최장 스트릭 : " + maxStreak +"일";

            }
            else{
                userMessage += "잘못된 값입니다.";
            }
        }
        catch(NullPointerException e){
            userMessage += "유저 정보가 잘못되었습니다. 올바른 값을 입력해주세요.";
        }
        catch(WebClientResponseException.BadRequest e){
            log.info("Exception : {" + e + "}");
            userMessage += "유저 닉네임이 잘못되었습니다. 올바른 값을 입력해주세요.";
        }
        catch(WebClientRequestException e){
            log.info("Exception : {}", e);
            userMessage += "서버 오류로 정보를 받아올 수 없습니다.";
        }
        catch(Exception e){
            log.info("Exception : {}", e);
            userMessage += "예상치 못한 이유로 정보를 받아올 수 없습니다.";
        }
        finally{
            return userMessage;
        }
    }

    // getProblem 전용 함수.
    // problem을 받아올때 JSONArray형태로 받아오기 때문에 idx를 고르기 위한 함수임.
    public void setCurrentObject(int idx){
        if(response != null){
            curObject = (JSONObject) response.get(idx);
            tagArray = curObject.getJSONArray("tags");
        }
    }

    // 문제의 티어만 받아오는 함수
    // DB 저장용
    public String getTier() {
        int level = (Integer)curObject.get("level");
        if(level == 0) return "Unrated";
        else if(0<level && level<=5) return "Bronze";
        else if(5<level && level<=10) return "Silver";
        else if(10<level && level<=15) return "Gold";
        else if(15<level && level<=20) return "Platinum";
        else if(20<level && level<=25) return "Diamond";
        else if(25<level && level<=30) return "Ruby";
        else return "Unrated";
    }

    public int getNum(){
        int num = (Integer) curObject.get("problemId");
        return num;
    }

    // 문제의 구체적인 티어 이모티콘을 표시하기 위한 함수
    // 반드시 슬랙에 해당 이모티콘을 추가해야 작동함
    // 자세한 설명은 README.md 참고할 것
    // Ex) Gold 2 -> :g2:, Ruby 5 -> :r5:
    public String getLevel() {
        String message = "";
        int level = (Integer) curObject.get("level");
        int tier;

        if (level != 0) {
            int tierlevel = level % 5 != 0 ? level / 5 : level / 5 - 1;
            message += getTier[tierlevel];
            tier = level % 5;
            if (tier == 0) message += "1: ";
            else message += (5 - tier + 1) + ": ";
        } else {
            message += "Unrated";
        }

        return message;
    }

    // 문제의 구체적인 등급을 숫자로 받아오는 함수 (SolvedAC api 문서 참고)
    // DB 저장용
    // EX) B5:1, B4:2, B2:4, S5:6, G5:11.....
    public int getLevelNumber() {
        int level = (Integer) curObject.get("level");
        return level;
    }

    // 문제 제목을 받아오는 함수
    public String getTitle() {
        String title = (String) curObject.get("titleKo");
        return title;
    }

    // 문제 푼 사람의 수를 받아오는 함수
    public int getAcceptedUser(){
        int acceptedUser = (Integer)curObject.get("acceptedUserCount");
        return acceptedUser;
    }

    // 문제의 알고리즘 분류를 받아오는 함수 (명령어용)
    public String getTags(){
        String message = "";
        ArrayList<String> type = new ArrayList<>();

        for(int i = 0; i<tagArray.length(); i++){
            JSONObject tags = (JSONObject) tagArray.get(i);
            JSONObject displaynames = (JSONObject) tags.getJSONArray("displayNames").get(0);
            String sort = (String)displaynames.get("name");
            type.add(sort);
        }

        if(type.contains("그래프 탐색")) message += "DFS&BFS";
        else if(type.contains("그래프 이론") || type.contains("이분 매칭")) message += "그래프 이론";
        else if(type.contains("다이나믹 프로그래밍")) message += "DP";
        else if(type.contains("분할 정복") || type.contains("분할 정복을 이용한 거듭제곱") || type.contains("분할 정복을 사용한 최적화")) message += "분할 정복";
        else if(type.contains("구현") || type.contains("시뮬레이션") || type.contains("백트래킹")) message += "구현/시뮬레이션";
        else if(type.contains("자료 구조") || type.contains("트리")) message += "자료구조";
        else if(type.contains("수학") || type.contains("누적합")) message += "수학";
        else if(type.contains("정렬") || type.contains("스위핑")) message += "정렬";
        else if(type.contains("이분 탐색") || type.contains("슬라이딩 윈도우") || type.contains("두 포인터")) message += "정렬";
        else if(type.contains("문자열")) message += "문자열";
        else if(type.contains("기하학") || type.contains("삼분 탐색")) message += "기하학";
        else message += "공개 불가";

        return message;
    }

    // getProblem을 호출했을때 슬랙에 보내지는 메시지
    public String getLevelMessage(){
        if(response != null){
            levelMessage = "";

            String level = getLevel();
            levelMessage += level;

            String title = getTitle();
            levelMessage += " " + title;

            int acceptedUser = getAcceptedUser();
            levelMessage += " (맞은 사람 : " + acceptedUser + "명)";

            String tag = getTags();
            levelMessage += "\n알고리즘 : " + tag;

            int num = getNum();
            levelMessage += "\n" + "https://www.acmicpc.net/problem/" + num + "\n";

            return levelMessage;
        }
        else{
            return levelMessage;
        }
    }
}

