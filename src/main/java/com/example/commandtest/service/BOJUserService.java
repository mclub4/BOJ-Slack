package com.example.commandtest.service;

import com.example.commandtest.sbb.BOJUser;
import com.example.commandtest.sbb.BOJUserRepository;
import com.example.commandtest.sbb.ProblemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.AopInvocationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DB에 등록된 유저를 다루는 서비스
 */

@RequiredArgsConstructor
@Service
public class BOJUserService {

    private final BOJUserRepository bojUserRepository;
    private final ProblemRepository problemRepository;

    // BOJcrawl에서 active 유저만 확인하기 위함
    public Map<String, String[]> getActiveUsers(boolean isActivate){
        ArrayList<BOJUser> bojUsers = bojUserRepository.findByActivation(isActivate);

        Map<String, String[]> selectedUsers = new HashMap<>();
        for(BOJUser bojUser : bojUsers){
            String BOJID = bojUser.getBOJID();
            String SlackID = bojUser.getSlackID();
            String activation = bojUser.isActivation()?"activate":"deactivate";
            String name = bojUser.getName();
            selectedUsers.put(BOJID, new String[]{SlackID, activation, name});
        }

        return selectedUsers;
    }

    // 특정 유저 알림 활성화
    @Transactional
    public boolean activateUser(String admin, String name){
        // 관리자만 알림 끄고 키게하고 싶으면 해당 부분에 슬렉ID를 추가할 것!
        // 누구나 알림 끄고 키게하고 싶으면 해당 부분을 삭제 할 것!
        // admin 관련은 추후 변경 예정
        if(!admin.equals("mclub901")) return false;

        BOJUser bojUser = this.bojUserRepository.findByBOJID(name);
        if(bojUser == null) return false;

        bojUser.setActivation(true);
        this.bojUserRepository.save(bojUser);

        return true;
    }

    // 특정 유저 알림 비활성화
    @Transactional
    public boolean deactivateUser(String admin, String name){
        // 관리자만 알림 끄고 키게하고 싶으면 해당 부분에 슬렉ID를 추가할 것!
        // 누구나 알림 끄고 키게하고 싶으면 해당 부분을 삭제 할 것!
        // admin 관련은 추후 변경 예정
        if(!admin.equals("mclub901")) return false;

        BOJUser bojUser = this.bojUserRepository.findByBOJID(name);
        if(bojUser == null) return false;

        bojUser.setActivation(false);
        this.bojUserRepository.save(bojUser);

        return true;
    }

    // 유저 정보를 추가하거나 업데이트 함
    @Transactional
    public boolean addUser(String admin, String query){
        // admin 관련은 추후 변경 예정
        if(!admin.equals("mclub901")) return false;

        String[] tmp = query.split(" ");

        if(tmp.length != 4) return false;
        if(!tmp[3].equals("true") && !tmp[3].equals("false")) return false;

        BOJUser bojUser = new BOJUser();
        bojUser.setBOJID(tmp[0]);
        bojUser.setSlackID(tmp[1]);
        bojUser.setName(tmp[2]);
        bojUser.setAdmin(Boolean.parseBoolean(tmp[3]));
        bojUser.setActivation(true);
        this.bojUserRepository.save(bojUser);

        return true;
    }

    // 특정 유저 정보를 삭제함
    @Transactional
    public boolean deleteUser(String admin, String name){
        if(!admin.equals("mclub901")) return false;

        BOJUser bojUser = this.bojUserRepository.findByBOJID(name);
        if(bojUser == null) return false;

        this.bojUserRepository.delete(bojUser);

        return true;
    }

    // 등록된 유저 정보와 추천 문제 목록 개요를 받아옴
    public String slackInfo(){
        List<BOJUser> bojUsers = this.bojUserRepository.findAll();

        String message = "[ 등록된 유저 알림 상태 ]\n\n";

        if(bojUsers.size() == 0){
            message += "등록된 유저가 존재하지 않습니다.";
        }
        else {
            int i = 1;
            for(BOJUser user : bojUsers){
                message += i + ". " + user.getBOJID() + " (" + user.getName() + ") : " + (user.isActivation() ? "활성화":"비활성화") + "\n";
                i++;
            }
        }

        message += "\n\n[ 등록된 추천 문제 정보 ]\n";

        int count = 0;

        String[] tiers = {"Bronze", "Silver", "Gold", "Platinum", "Diamond", "Ruby"};

        for(String tier : tiers){
            int now = 0;
            try{
                now = this.problemRepository.countTier(tier);
            }
            catch(AopInvocationException e){
                now = 0;
            }
            finally{
                count += now;
                message += tier + " : " + now + "개\n";
            }
        }

        message += "총 문제 개수 : " + count + "개";

        return  message;
    }
}
