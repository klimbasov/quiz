package com.innowise.quiz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("quiz")
public class ServicePropertySource {
    private int pageSize = 20;
    private String secret = "default";
}
