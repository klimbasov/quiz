package com.innowise.quiz.domain.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan("com.innowise.quiz.domain")
public class DomainConfig {
}
