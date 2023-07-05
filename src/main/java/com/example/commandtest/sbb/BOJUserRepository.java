package com.example.commandtest.sbb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface BOJUserRepository extends JpaRepository<BOJUser, Integer>{
    BOJUser findByBOJID(String BOJID);

    ArrayList<BOJUser> findByActivation(boolean isActivate);
}
