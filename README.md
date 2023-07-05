# BOJ Slack BOT


1일 1백준 슬랙 채널에 백준 문제를 풀지 않은 사람에게 자동으로 알람을 보내기 위해 만들었음.

본 프로젝트는 [BOJ 슬랙봇 만들기](https://velog.io/@chlee4858/BOJ-%EC%8A%AC%EB%9E%99%EB%B4%87-%EB%A7%8C%EB%93%A4%EA%B8%B0)글에서 영감을 얻어 Spring Boot, Slash command 등을 추가적으로 적용하여 CloudType에 올려 사용중임.

git action을 통한 지속적인 CI/CD가 이루어지도록 할 예정.

## 사전 준비

- 반드시 Jdk가 설치되어있어야함!! 
- Java 17 기준으로 제작되었음. 반드시 Java 17이여야함!!
- Gradle 8.1.1 ver 기준으로 제작되었음 (Build시 Gradle Wrapper를 이용했기 때문에 설치되지 않아도 됨)

## 빌드 및 실행하기

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

## 디렉토리 구조 (src아래만)


## 주요 기능

## 개발 가이드

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
