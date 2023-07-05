package com.example.commandtest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class Response {
    private String text;
    private String response_type;
}
