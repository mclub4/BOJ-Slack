package com.example.commandtest.config;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

/**
 * Token, ChannelID, TeamID를 관리할 properties를 다루는 Class
 */

public class EnvManager {
    private Properties properties;
    // build시, jar 파일 기준 경로임. 만약에 Intellij에서 빌드할 경우, ../../을 제거할 것.
//    private final String path = "./src/main/resources/env.properties";

    // 아래 코드는 jar 빌드 전용 코드
    private final String path = "../../src/main/resources/env.properties";

    public EnvManager() throws IOException {
        this.properties = new Properties();

        try{
            properties.load(new FileInputStream(path));
            System.out.println(getValueByKey("token"));
        }
        // 최초 실행시, env.properties 파일 생성
        catch (FileNotFoundException e) {
            Path currentPath = Paths.get("");
            String path = currentPath.toAbsolutePath().toString();
            System.out.println("현재 작업 경로: " + path);
            System.out.println(e.toString() + "이 발생했습니다!");
//            File file = new File(path);
//            file.createNewFile();
//            properties.load(new FileInputStream(path));
        }
        catch(IOException e){
            Path currentPath = Paths.get("");
            String path = currentPath.toAbsolutePath().toString();
            System.out.println("현재 작업 경로: " + path);
            System.out.println(e.toString() + "이 발생했습니다!");
//            File file = new File(path);
//            file.createNewFile();
//            properties.load(new FileInputStream(path));
        }
    }

    // Token, ChannelID, TeamID 등 등록된 정보를 가져옴.
    public String getValueByKey(String key){
        return this.properties.getProperty(key);
    }

    public Set<String> getKeyList() {
        return this.properties.stringPropertyNames();
    }

    public boolean isKeyExist(String key){
        return this.properties.containsKey(key);
    }

    // 최초 실행시, env.properties에 키값 등록 시켜주는 용도
    // 최초 실행시 반드시 등록할 것!!
    public boolean addProperty(String key, String value){
        return this.properties.putIfAbsent(key, value) == null ? true : false;
    }

    public boolean removePropertyByKey(String key) {
        return this.properties.remove(key) == null ? false : true;
    }

    public boolean editPropertyValueByKey(String key, String value) {
        return this.properties.replace(key, value) == null ? false : true;
    }

    // addProperty를 모두 마친 후, env.properties 파일에 저장
    // 최초 실행시 반드시 등록할 것!
    public void savePropertiesFile() {
        try {
            this.properties.store(new FileOutputStream(path), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
