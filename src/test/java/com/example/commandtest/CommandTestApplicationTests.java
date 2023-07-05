package com.example.commandtest;

import com.example.commandtest.bojcrawl.BOJProblems;
import com.example.commandtest.bojcrawl.BOJcrawl;
import com.example.commandtest.bojcrawl.SolvedAC;
import com.example.commandtest.sbb.BOJProblem;
import com.example.commandtest.sbb.ProblemRepository;
import com.example.commandtest.sbb.BOJUserRepository;
import com.example.commandtest.sbb.BOJUser;
import com.example.commandtest.service.BOJUserService;
import com.example.commandtest.service.ProblemService;
import com.example.commandtest.service.RecommendService;
import com.example.commandtest.slack.SlacksendMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class CommandTestApplicationTests {

	@Autowired
	private BOJUserRepository userRepository;

	@Autowired
	private ProblemRepository problemRepository;

	@Autowired
	private ProblemService problemService;

	@Autowired
	private RecommendService recommendService;

	@Autowired
	private BOJUserService bojUserService;

	@Autowired
	private BOJcrawl boJcrawl;

	@Test
	// DB에 유저 정보 넣기
	void userLoads() {
		final Map<String, String[]> members = new HashMap(){{
			put("mclub4", new String[]{"U050X2WQCD7", "activate", "조현진"});
			put("ehensnfl",  new String[]{"U050LTRB69J", "activate", "홍석주"});
			put("sunberbee123",  new String[]{"U050JECM4AE", "deactivate", "이현준"});
			put("pjs4813",  new String[]{"U050X34U273", "activate", "박준석"});
			put("jjhinu104",  new String[]{"U050X321HED", "activate", "정지환"});
			put("ho44013",  new String[]{"U053ZKT8D46", "deactivate", "김성호"});
			put("changyon99",  new String[]{"U051EELPCF7", "deactivate", "최창연"});
			put("noye",  new String[]{"U054Z1PGJ8H", "deactivate", "한창훈"});
			put("doo620",  new String[]{"U0519KP5R8D", "activate", "최지훈"});
			put("chldudfkr119",  new String[]{"U050X30LGF3", "activate", "최영락"});
			put("sam1006", new String[]{"U055W9B3XH8", "activate", "조보국"});
			put("jungus07",  new String[]{"U05626WUJ0L", "activate", "정의석"});
		}};

		for(String member : members.keySet()){
			BOJUser user = new BOJUser();
			System.out.println(member);
			user.setBOJID(member);
			user.setSlackID(members.get(member)[0]);
			user.setActivation(members.get(member)[1].equals("activate") ? true : false);
			user.setName(members.get(member)[2]);
			user.setAdmin(member.equals("mclub4") ? true:false);
			System.out.println(user.getName());
			System.out.println(user.getSlackID());
			System.out.println(user.getBOJID());
			this.userRepository.save(user);
		}
	}

	@Test
	// DB에 문제 추천 리스트 넣기
	void problemLoads(){
		BOJProblems bojProblems = new BOJProblems();
		int idx = 0;
		int length = bojProblems.problems.length;

		while(idx<length){
			StringBuilder sb = new StringBuilder();
			int end = idx+100 < length ? idx+100 : length;

			System.out.println(idx + ", " + length + ", " + end);

			for(int i = idx; i< end; i++){
				sb.append(bojProblems.problems[i] + ",");
			}
			String tmp = sb.toString();
			String params = tmp.substring(0, tmp.length()-1);
			SolvedAC solvedAC = new SolvedAC();
			System.out.println(params);
			solvedAC.getProblem(params);

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
	}

	@Test
	void problemAdd1(){
		String result = problemService.deleteProblem("14636");
		assert(result.equals("해당 문제를 찾을 수 없습니다."));

		result = problemService.addProblem("12");
		assert(result.equals("잘못된 문제 번호를 제외하고 추천목록에 추가되었습니다."));

		result = problemService.addProblem("14636 14751 10067");
		assert(result.equals("모든 문제가 정상적으로 추가되거나 업데이트 되었습니다."));

		result = problemService.deleteProblem("14636");
		assert(result.equals("해당 문제가 삭제되었습니다."));
	}

	@Test
	void BOJcrawlTest() throws IOException, URISyntaxException {
		ArrayList<String> test = boJcrawl.checkunSolvedUsers(LocalDateTime.now());

		for(String tmp : test){
			System.out.println(tmp);
		}
	}

	@Test
	void slackInfo(){
		bojUserService.deactivateUser("mclub901", "mclub4");
		System.out.println(bojUserService.slackInfo());
		bojUserService.activateUser("mclub901", "mclub4");
	}

	@Test
	void slack(){
		String tmp = bojUserService.slackInfo();
		System.out.println(tmp);
	}

	@Test
	void slackSendTest() throws Exception {
		SlacksendMessage slacksendMessage = new SlacksendMessage();
		slacksendMessage.sendSlack(5, "Test");
	}

	@Test
	void randomLevelTest() throws IOException {
		String a = recommendService.randomLevel("  p3 4  ");
		System.out.println(a);
		String b = recommendService.randomLevel("5");
		System.out.println(b);
		String c = recommendService.randomLevel("g");
		System.out.println(c);
		String d = recommendService.randomRepeat();
		System.out.println(d);
		String e = recommendService.randomLevel("");
		System.out.println(e);
	}

	// 문제 이상한 파라미터 들어줬을때 체크
	@Test
	void solvedacProblem1(){
		try{
			SolvedAC solvedAC = new SolvedAC();
			solvedAC.getProblem("MessiIsGod");
			solvedAC.setCurrentObject(0);
			System.out.println(solvedAC.getLevelMessage());
			String tier = solvedAC.getTier();
			assert(false);
		}
		catch(NullPointerException e){
			assert(true);
		}
	}

	// 정상적인 파라미터 주어졌을때 체크
	@Test
	void solvedacProblem2(){
		SolvedAC solvedAC = new SolvedAC();
		solvedAC.getProblem("24025");
		solvedAC.setCurrentObject(0);
		System.out.println(solvedAC.getLevelMessage());
		String tier = solvedAC.getTier();
		assert(tier.equals("Gold"));
	}

	@Test
	void solvedacUser1(){
		SolvedAC solvedAC = new SolvedAC();
		String tmp = solvedAC.getUser("MessiIsMapleStroyLover");
		assert(tmp.equals("유저 닉네임이 잘못되었습니다. 올바른 값을 입력해주세요."));
	}

	@Test
	void solvedacUser2(){
		SolvedAC solvedAC = new SolvedAC();
		String tmp = solvedAC.getUser("mclub4");
		System.out.println(tmp);
	}

	@Test
	void random(){
		SolvedAC solvedAC = new SolvedAC();

		List<BOJProblem> response = problemRepository.findAll();
		for(BOJProblem i : response){
			System.out.println(i.getNum());
			solvedAC.getProblem(Integer.toString(i.getNum()));
			solvedAC.setCurrentObject(0);
			System.out.println(solvedAC.getLevelMessage());
		}
	}
}
