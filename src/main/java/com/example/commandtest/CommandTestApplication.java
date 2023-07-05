package com.example.commandtest;

import com.example.commandtest.timer.GetDate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommandTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(CommandTestApplication.class, args);
		System.out.println(getGreeting());
	}

	public static String getGreeting() {
		GetDate getDate = new GetDate();
		String test = getDate.test();
		return "현재시간 " + test + ", Slack BOJ 봇을 실행합니다.";
	}
}
