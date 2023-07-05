package com.example.commandtest.sbb;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BOJProblem {
    @Id
    @Column(length = 10)
    private int num;

    @Column(length = 300)
    private String title;

    @Column(length = 20)
    private String tier;

    @Column(length = 20)
    private int level;

    @Column(length = 200)
    private String tag;
}
