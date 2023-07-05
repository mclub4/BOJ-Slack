package com.example.commandtest.service;

import com.example.commandtest.bojcrawl.SolvedAC;
import com.example.commandtest.sbb.BOJProblem;
import com.example.commandtest.sbb.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DB의 문제 추천 목록에서 골라주는 서비스
 */

@RequiredArgsConstructor
@Service
public class RecommendService {
    private final ProblemRepository problemRepository;
    private final SolvedAC solvedAC;

    public String randomLevel(String v){
        if(v.equals("")) randomNumber(1);

        v = v.trim();
        String[] split = v.split(" ");
        int num = 1;

        if(v != null && !v.equals("")){
            int flag = 1;

            if(split.length == 1){
                try{
                    num = Integer.parseInt(split[0]);
                    flag = 0;
                }
                catch (NumberFormatException e){
                    assert(true);
                }
            }
            else if(split.length >= 2){
                try{
                    num = Integer.parseInt(split[1]);
                    if(num>5) return "최대 5문제까지만 추천 가능합니다.";
                }
                catch (NumberFormatException e){
                    return "잘못된 인자입니다.";
                }
            }

            v = split[0].toLowerCase().replaceAll(" ", "");
            String param = "";
            int levelparam = 0;

            if(flag == 1){
                if(v.equals("bronze") || v.equals("b")) param = "Bronze";
                else if(v.equals("silver") || v.equals("s")) param = "Silver";
                else if(v.equals("gold") || v.equals("g")) param = "Gold";
                else if(v.equals("platinum") || v.equals("p")) param = "Platinum";
                else if(v.equals("diamond") || v.equals("d")) param = "Diamond";
                else if(v.equals("ruby") || v.equals("r")) param = "Ruby";
                else flag = 2;
            }

            if(flag == 2){
                String tier = v.substring(0,v.length()-1);
                int level;
                try{
                    level = Integer.parseInt(v.substring(v.length()-1, v.length()));
                    if(!(1<= level && level<=5)) return "잘못된 인자입니다.";
                }
                catch (NumberFormatException e){
                    return "잘못된 인자입니다.";
                }
                if(tier.equals("b") || tier.equals("bronze")) levelparam = 6- level;
                else if(tier.equals("s") || tier.equals("silver")) levelparam = 11 - level;
                else if(tier.equals("g") || tier.equals("gold")) levelparam = 16 - level;
                else if(tier.equals("p") || tier.equals("platinum")) levelparam = 21 - level;
                else if(tier.equals("d") || tier.equals("diamond")) levelparam = 21 - level;
                else if(tier.equals("r") || tier.equals("ruby")) levelparam = 21 - level;
                else return "잘못된 인자입니다.";
            }

            if(flag == 0){
                List<BOJProblem> recommendation = problemRepository.random(num);
                if(recommendation.size() != 0){
                    String levelMessage = "";
                    for(int i = 0; i<recommendation.size(); i++){
                        BOJProblem problem = recommendation.get(i);
                        solvedAC.getProblem(Integer.toString(problem.getNum()));
                        solvedAC.setCurrentObject(0);
                        levelMessage += solvedAC.getLevelMessage();
                    }
                    return levelMessage;
                }
                else return "알 수 없는 오류가 발생했습니다.";
            }
            else if(flag == 1) {
                List<BOJProblem> recommendation = problemRepository.randomTier(param, num);
                if(recommendation.size() != 0){
                    String levelMessage = "";
                    for(int i = 0; i<recommendation.size(); i++){
                        BOJProblem problem = recommendation.get(i);
                        solvedAC.getProblem(Integer.toString(problem.getNum()));
                        solvedAC.setCurrentObject(0);
                        levelMessage += solvedAC.getLevelMessage();
                    }
                    return levelMessage;
                }
                else return "추천 목록에 해당 조건의 문제가 존재하지 않습니다.";
            }
            else if(flag == 2){
                List<BOJProblem> recommendation = problemRepository.randomLevel(levelparam, num);
                if(recommendation.size() != 0){
                    String levelMessage = "";
                    for(int i = 0; i<recommendation.size(); i++){
                        BOJProblem problem = recommendation.get(i);
                        solvedAC.getProblem(Integer.toString(problem.getNum()));
                        solvedAC.setCurrentObject(0);
                        levelMessage += solvedAC.getLevelMessage();
                    }
                    return levelMessage;
                }
                else return "추천 목록에 해당 조건의 문제가 존재하지 않습니다.";
            }
        }
        else return randomNumber(num);

        return "알 수 없는 오류가 발생했습니다.";
    }

    public String randomNumber(int num){
        List<BOJProblem> recommendation = problemRepository.random(num);
        BOJProblem problem = recommendation.get(0);
        solvedAC.getProblem(Integer.toString(problem.getNum()));
        solvedAC.setCurrentObject(0);
        return solvedAC.getLevelMessage();
    }

    public String randomRepeat(){
        List<BOJProblem> recommendation = problemRepository.randomRepeat();
        if(recommendation.size() != 0){
            String levelMessage = "즐거운 점심입니다. 맛있는 점심 드시고 이런 문제들 풀어보는건 어떨까요?\n\n";
            for(int i = 0; i<recommendation.size(); i++){
                BOJProblem problem = recommendation.get(i);
                solvedAC.getProblem(Integer.toString(problem.getNum()));
                solvedAC.setCurrentObject(0);
                levelMessage += solvedAC.getLevelMessage();
            }
            return levelMessage;
        }
        else return "알 수 없는 오류가 발생했습니다.";
    }
}
