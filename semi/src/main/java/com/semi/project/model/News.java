package com.semi.project.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class News {
    private String time;
    private String subject;
    private String url;
}
