package com.example.commandtest.sbb;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BOJUser {
    @Id
    @Column(length = 100)
    private String BOJID;

    @Column(length = 50)
    private String SlackID;

    @Column
    private boolean activation;

    @Column(length = 10)
    private String name;

    @Column
    private boolean admin;
}
