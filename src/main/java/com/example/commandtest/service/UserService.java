package com.example.commandtest.service;

import com.example.commandtest.bojcrawl.SolvedAC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SolvedAC의 유저 정보를 검색하는 서비스
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    public String getUser(String ID){
        SolvedAC solvedAC = new SolvedAC();
        String profile = solvedAC.getUser(ID);
        log.info("User : {}", profile);

        return profile;
    }
}

