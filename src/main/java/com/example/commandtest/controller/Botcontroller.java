package com.example.commandtest.controller;

import com.example.commandtest.dto.*;
import com.example.commandtest.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/slack")
@RequiredArgsConstructor
public class Botcontroller {

    private final ProblemService problemService;
    private final RecommendService recommendService;
    private final UserService userService;
    private final BOJUserService bojUserService;

    /**
    // Problem 또는 User 객체 : Slash Command를 통해 도착한 정보들, 자세한 내용은 Slack API 홈페이지 확인
    // Response 객체 : Message를 전달하기 위한 객체.
    // -> in_channel : 전체 공개로 보냄, ephemeral : 요청한 개인에게만 보임
     */

    // 백준 문제 정보 받아오기
    @PostMapping(value="/problem", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response problem_message(Request request){
        String num = request.getText();

        String message = problemService.getProblem(num);
        log.info("problem : {}", request.getUser_name());
        log.info("problemNum : {}", num);

        return new Response(message, "in_channel") ;
    }

    // 백준 유저 정보 받아오기
    @PostMapping(value="/user", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response user_message(Request request){
        String ID = request.getText();
        String profile = userService.getUser(ID);

        log.info("user : {}", request.getUser_name());
        log.info("userID : {}", ID);

        return new Response(profile, "in_channel") ;
    }

    // 문제 추천
    @PostMapping(value="/recommend", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response recommend(Request request){
        String query = request.getText();
        String message = recommendService.randomLevel(query);

        log.info("recommend : {}", request.getUser_name());
        log.info("query : {}", query);

        return new Response(message, "ephemeral");
    }

    // Solved AC처럼 쿼리로 문제 검색 하기
    // TODO : solvedAC lookup api로 만들기
    @PostMapping(value="/lookup", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response search(Request request){
        String message = "현재 제작중인 기능입니다.";

        log.info("lookup : {}", request.getUser_name());

        return new Response(message, "in_channel");
    }

    // DB에 문제 추가하기
    @PostMapping(value="/addProblem", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response addProblem(Request request){
        String num = request.getText();
        String message = problemService.addProblem(num);

        log.info("addProblem : {}", request.getUser_name());
        log.info("problemNum : {}", num);

        return new Response(message, "in_channel");
    }

    // DB에 문제 제거하기
    @PostMapping(value="/deleteProblem", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response deleteProblem(Request request){
        String num = request.getText();
        String message = problemService.deleteProblem(num);

        log.info("deleteProblem : {}", request.getUser_name());
        log.info("problemNum : {}", num);

        return new Response(message, "in_channel");
    }

    /**
    // Slack User CRUD
    */

    // 현재 유저 정보들 받아오기
    @PostMapping(value="/userinfo", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response userinfo(){
        String message = bojUserService.slackInfo();

        return new Response(message, "in_channel");
    }

    // DB에 유저 추가하기
    @PostMapping(value="/addUser", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response addUser(Request request){
        String query = request.getText();
        String admin = request.getUser_name();
        String message;

        log.info("addUser : {}", request.getUser_name());
        log.info("query : {}", query);

        boolean correct = bojUserService.addUser(admin, query);

        if(correct) message = "정상적으로 추가되었습니다.";
        else message = "유저 등록에 실패하였습니다. (권한이 없거나 파라미터가 잘못되었습니다)";

        return new Response(message, "ephemeral");
    }

    // DB에 유저 제거
    @PostMapping(value="/deleteUser", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response deleteUser(Request request){
        String user = request.getText();
        String admin = request.getUser_name();
        String message;

        log.info("addUser : {}", request.getUser_name());
        log.info("user : {}", user);

        boolean correct = bojUserService.deleteUser(admin, user);

        if(correct) message = "정상적으로 삭제되었습니다.";
        else message = "유저 삭제에 실패하였습니다. (권한이 없거나 파라미터가 잘못되었습니다)";

        return new Response(message, "ephemeral");
    }

    // 알람 키기
    @PostMapping(value="/activate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response activate(Request request){
        String name = request.getText();
        String admin = request.getUser_name();
        String message;

        log.info("activate : {}", request.getUser_name());

        boolean correct = bojUserService.activateUser(admin, name);

        if(correct) message = name + "님의 알림이 활성화 되었습니다.";
        else message = admin + "님은 이 명령어를 사용할 권환이 없습니다.";

        return new Response(message, "ephemeral");
    }

    // 알람 끄기
    @PostMapping(value="/deactivate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response deactivate(Request request){
        String name = request.getText();
        String admin = request.getUser_name();
        String message;

        log.info("deactivate : {}", request.getUser_name());

        boolean correct = bojUserService.deactivateUser(admin, name);

        if(correct) message = name + "님의 알림이 비활성화 되었습니다.";
        else message = admin + "님은 이 명령어를 사용할 권환이 없습니다.";

        return new Response(message, "ephemeral");
    }

}
