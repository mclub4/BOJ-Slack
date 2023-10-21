# BOJ Slack BOT


1일 1백준 슬랙 채널에 백준 문제를 풀지 않은 사람에게 자동으로 알람을 보내기 위해 만들었음.

본 프로젝트는 [BOJ 슬랙봇 만들기](https://velog.io/@chlee4858/BOJ-%EC%8A%AC%EB%9E%99%EB%B4%87-%EB%A7%8C%EB%93%A4%EA%B8%B0)글에서 영감을 얻어 Spring Boot, Slash command 등을 추가적으로 적용하여 CloudType에 올려 사용 중 이었으나 아래 개발 가이드에 작성되있는 이유로 현재는 AWS로 변경하였음.

git action을 통한 지속적인 CI/CD가 이루어지도록 할 예정.

## 📌 사전 준비

- 반드시 Jdk가 설치되어있어야함!! 
- Java 17 기준으로 제작되었음. 반드시 Java 17이여야함!!
- Gradle 8.1.1 ver 기준으로 제작되었음 (Build시 Gradle Wrapper를 이용했기 때문에 설치되지 않아도 됨)

## 🔥 시작하기

<br/>

1. src/main/resources 폴더에 env.properties 파일을 만들기
2. Slack API 홈페이지에서 token 발급받기
3. 자신이 메시지를 보낼 team의 ID와 channel의 ID를 알아내기 (Slack API 홈페이지 들어가면 나와있음)
4. env.properties 폴더에 toekn = "토큰", teamID = "팀아이디", channelID = "채널아이디" 형식으로 각 줄에 적을 것
5. 이때, channel은 Slack의 Team마다 다르니 src/main/java/slack/SlackInfo.java 코드 부분의 getChannelID 부분을 수정할 것.
6. 실행전 db를 생성하고 Test의 userLoads()와 problemLoads()를 실행할 것. 이때, userLoads()에는 팀원들의 SlackID 코드, 알림활성화 여부, 이름을 적으면 됨.

<br/>

## 🗂 빌드 및 실행

### 빌드
```groovy
./gradlew clean build
```

또는
```groovy
gradle clean build
```
### 실행
```cmd
java -jar app/build/libs/app.jar
```
</br>

## 🛠 개발 환경

<img src="https://img.shields.io/badge/spring-3776AB?style=for-the-badge&logo=Spring&logoColor=white">

</br>

## 🎥 주요 기능

- ### 문제를 풀지않은 그룹원에게 알림 보내기

![image](https://github.com/mclub4/BOJ-Slack/assets/55117706/b348a114-c97d-43cb-86a7-02feff61aed4)

특정 시간에 알람을 활성화한 문제를 풀지 않은 그룹원을 태그하여 알림을 보냄.

기본적으로 19시 30분, 23시, 00시에 알림을 보내도록 설정되어 있음.


- ### 문제 추천

![image](https://github.com/mclub4/BOJ-Slack/assets/55117706/0b43aeb8-464c-4a59-a620-cbb74c6eb660)

특정 시간에 추천 문제 DB에서 문제를 추천해줌. (추천문제 DB는 직접 추가해야됨)

기본적으로 12시에 문제를 추천해주도록 설정되있으며, 무작위 난이도로 3문제를 추천해줌. 이때, 한 문제는 무조건 골드 이하로 추천해줌. 

![image](https://github.com/mclub4/BOJ-Slack/assets/55117706/6286efd6-713a-4f55-83a2-ebdce8d73b4a)

또한, 유저 커맨드를 통하여 특정 시간이 아니더라도 문제를 추천 받을 수 있으며 /recommend gold 5 이런식으로 입력하여 특정 난이도의 문제를 원하는 수만큼 추천받을 수 있음.


- ### 백준 문제/유저 정보 가져오기

![image](https://github.com/mclub4/BOJ-Slack/assets/55117706/14b2c3d9-aa53-4080-92a1-6e90596bbd16)

유저 커맨드를 통하여 특정 문제의 정보를 받아올 수 있음.

이때, 문제 알고리즘 분류는 큰 범위의 알고리즘으로 알려주고 핵심적인 알고리즘은 알려주지 않음.

예를 들어, 위상 정렬이면 그래프 이론, 스택이면 자료구조, 이분 탐색이면 정렬, 모듈러의 역원이면 수학 이런식으로 표시함.

다만, 그리디같이 다른걸로 대체하기 어려울 경우 "공개 불가"로 표시함.

![image](https://github.com/mclub4/BOJ-Slack/assets/55117706/0e6f3d23-0fb5-49b5-9672-64a182fd29ad)

특정 백준 유저의 정보를 가져오는 것도 가능함.


## ⚠️ 실행 오류 해결 가이드

아래는 실행하는데 겪었던 에러들과 그에 대한 해결방법을 정리한 것임. 

### Build할때, CA 인증서 오류 발생시

```cmd
sun.security.validator.ValidatorException: PKIX path building failed:
```
```cmd
sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```

위와 같은 오류가 발생하면, Java의 신뢰하는 인증서 목록(keystore)에 사용하고자 하는 인증기관이 등록되어 있지 않아 접근이 차단되는 현상이다.

```cmd
%JAVA_HOME%/lib/security/cacerts
```

위와 같은 경로에 slack.com에 대한 CA 인증서를 추가해주면 된다. 자세한 내용은 밑에 글을 참고할 것.

[JAVA에 CA 인증서 추가 하는 방법](https://www.lesstif.com/java/java-pkix-path-building-failed-98926844.html)

### SolvedAC Api 403 에러

해당 에러는 정확한 원인을 알 순 없으나, 클라우드 서비스 중 구글 클라우드를 사용하였을때 발생하는 에러로 추정됨. Cloudtype도 구글 클라우드 기반이라 해당 에러가 발생하였음.

AWS, Azure같은 타 클라우드 서비스를 이용하면 해결가능.

### 클라우드 서비스 미사용시, 외부 아이피를 통한 접근 불가

포트포워딩을 하지 않았을 가능성이 매우 높음. 해당 글을 참고하여 설정 할 것.

[iptime 공유기 포트포워딩 하는 방법](https://velog.io/@moey920/iptime-%EA%B3%B5%EC%9C%A0%EA%B8%B0-%ED%8F%AC%ED%8A%B8-%ED%8F%AC%EC%9B%8C%EB%94%A9-%EC%84%A4%EC%A0%95)

만약 포트포워딩을 했음에도 불구하고 접속이 불가능하다면, 이중 공유기일 가능성이 높기 때문에 밑에 글을 참고할 것.

[포트포워딩 이중 공유기 관련 문제](https://sandn.tistory.com/83)

위 방법으로도 접속이 불가능하다면, 아마 오래된 아파트에서는 집에 있는 공유기말고, 외부에 다른 공유기가 있을 가능성이 매우 높음. 해당 경우에는 아파트 자체의 공유기를 포트포워딩을 해야되므로 개인이 불가능한 영역임. 

즉, 이 경우에는 어쩔 수 없이 AWS, Azure와 같은 클라우드 서비스를 이용해야함.

### 플랫폼에 따른 Connection Timeout

해당 에러는 Rasberry Pi와 같이 성능이 안좋은 성능의 기기에서 실행할 경우 발생함. ConnectionTimeout 시간을 늘려주면 해결됨.

### AWS EC2 프리티어 사용시, build 되는데 시간이 오래걸림

아래 글을 참고하여 사용 메모리를 늘릴 것.

[AWS EC2 메모리 늘리기](https://velog.io/@shawnhansh/AWS-EC2-%EB%A9%94%EB%AA%A8%EB%A6%AC-%EC%8A%A4%EC%99%91)
