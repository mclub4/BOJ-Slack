package com.example.commandtest.service;

import com.example.commandtest.bojcrawl.SolvedAC;
import com.example.commandtest.sbb.BOJProblem;
import com.example.commandtest.sbb.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SolvedAC의 문제를 검색하거나 추천목록 DB에 저장/삭제하는 서비스
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class ProblemService {

    private final ProblemRepository problemRepository;

    public String getProblem(String num){
        SolvedAC solvedAC = new SolvedAC();
        solvedAC.getProblem(num);
        solvedAC.setCurrentObject(0);

        String level = solvedAC.getLevelMessage();
        log.info("User : {}", level);

        return level;
    }

    public String addProblem(String num){
        if(num.equals("")) return "삭제할 문제 번호를 입력해주세요.";

        String[] problemList = num.split(" ");

        int idx = 0;
        int length = problemList.length;
        int result = 0;

        while(idx<length){
            StringBuilder sb = new StringBuilder();
            int end = idx+100 < length ? idx+100 : length;

            for(int i = idx; i< end; i++){
                sb.append(problemList[i] + ",");
            }
            String tmp = sb.toString();
            String params = tmp.substring(0, tmp.length()-1);
            SolvedAC solvedAC = new SolvedAC();
            solvedAC.getProblem(params);
            result += solvedAC.response.length();

            for(int i = 0; i<solvedAC.response.length(); i++){
                solvedAC.setCurrentObject(i);
                BOJProblem problem = new BOJProblem();
                problem.setNum(solvedAC.getNum());
                problem.setTitle(solvedAC.getTitle());
                problem.setTier(solvedAC.getTier());
                problem.setLevel(solvedAC.getLevelNumber());
                problem.setTag(solvedAC.getTags());
                this.problemRepository.save(problem);
            }

            idx += 100;
        }

        if(result == length) return "모든 문제가 정상적으로 추가되거나 업데이트 되었습니다.";
        if(length == 1) return "문제 번호가 잘못되었습니다.";
        return "잘못된 문제 번호를 제외하고 추천목록에 추가되었습니다.";
    }

    public String deleteProblem(String num){
        if(num.equals("")) return "삭제할 문제 번호를 입력해주세요.";

        String[] tmp = num.split(" ");
        if(tmp.length >=2) return "삭제는 한번에 1개만 할 수 있습니다.";

        BOJProblem boJproblem = problemRepository.findByNum(Integer.parseInt(num));
        if(boJproblem != null){
            problemRepository.delete(boJproblem);
            return "해당 문제가 삭제되었습니다.";
        }
        else{
            return "해당 문제를 찾을 수 없습니다.";
        }
    }
}
