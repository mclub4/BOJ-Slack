package com.example.commandtest.sbb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepository extends JpaRepository<BOJProblem, Integer> {

    // 일반적인 문제 추천
    @Query(value = "SELECT * FROM BOJPROBLEM order by RAND() limit ?1",nativeQuery = true)
    List<BOJProblem> random(int num);

    // 티어 파라미터가 주어졌을때, 그 티어 문제 추천해주는 기능
    @Query(value = "SELECT * FROM BOJPROBLEM WHERE Tier= ?1 order by RAND() limit ?2",nativeQuery = true)
    List<BOJProblem> randomTier(String tier, int num);

    // 레벨 파라미터가 주어졌을때, 그 레벨 이하 문제 추천해주는 기능
    @Query(value = "SELECT * FROM BOJPROBLEM WHERE Level<= ?1 order by RAND() limit ?2",nativeQuery = true)
    List<BOJProblem> randomLevel(int level, int num);

    // 정기적인 문제 추천
    @Query(value = "WITH TMP AS(SELECT * FROM BOJPROBLEM WHERE Level<= 14 AND Level>= 6 ORDER BY RAND() LIMIT 1)," +
            "TMP2 AS(SELECT * FROM BOJPROBLEM WHERE LEVEL>=6 AND LEVEL<=25 AND NUM NOT IN (SELECT NUM FROM TMP) ORDER BY RAND() LIMIT 2)" +
            "SELECT * FROM TMP UNION ALL SELECT * FROM TMP2",nativeQuery = true)
    List<BOJProblem> randomRepeat();

    BOJProblem findByNum(int num);

    @Query(value = "SELECT COUNT(*) FROM BOJPROBLEM WHERE TIER = ?1 GROUP BY TIER", nativeQuery = true)
    int countTier(String tier);
}
