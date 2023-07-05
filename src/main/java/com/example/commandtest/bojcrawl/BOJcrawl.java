package com.example.commandtest.bojcrawl;

import com.example.commandtest.config.ParameterHelper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.commandtest.service.BOJUserService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BOJcrawl{

    @Autowired
    private final BOJUserService bojUserService;

    // 등록된 맴버들이 가장 최근에 문제를 푼 시간을 가져옴
    public LocalDateTime crawlSolvedUsers(String name) throws IOException, URISyntaxException {
        String base = "https://www.acmicpc.net/status";
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("problems_id", "");
        parameters.put("user_id", name);
        parameters.put("language_id", "-1");
        parameters.put("result_id", "4");

        String url = base + "?" + ParameterHelper.getParamsString(parameters);

        Document doc = Jsoup.parse(new URI(url).toURL(), 20000);
        Elements elements = doc.select("table#status-table>tbody>tr>td>a.show-date");
        String time = elements.first().attr("title");

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsedDate = LocalDateTime.parse(time, dateTimeFormatter);
        return parsedDate;
    }

    // 풀지 않은 맴버들의 슬랙 ID를 가져옴
    public ArrayList<String> checkunSolvedUsers(LocalDateTime START) throws IOException, URISyntaxException {
        Map<String, String[]> members = bojUserService.getActiveUsers(true);

        ArrayList<String> users = new ArrayList<>();
        for(String member : members.keySet()){
            // BOJmembers에서 활성화된 맴버만 체크함
            if(members.get(member)[1].equals("activate")){
                // 백준에 가입하지 않은 맴버를 받아 올 경우, 그 맴버는 건너뜀
                try{
                    LocalDateTime time = crawlSolvedUsers(member);
                    if(time.isBefore(START)){users.add(members.get(member)[0]);}
                }
                catch (NullPointerException e){
                    continue;
                }
            }
        }
        return users;
    }

    // ConnectionException 테스트
    public void test() throws IOException, URISyntaxException {
        String base = "https://www.acmicpc.com/status";
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("problems_id", "");
        parameters.put("user_id", "mclub4");
        parameters.put("language_id", "-1");
        parameters.put("result_id", "4");

        String url = base + "?" + ParameterHelper.getParamsString(parameters);

        System.out.println("test");
        Document doc = Jsoup.parse(new URI(url).toURL(), 20000);
        Elements elements = doc.select("table#status-table>tbody>tr>td>a.show-date");
        String time = elements.first().attr("title");
    }
}
