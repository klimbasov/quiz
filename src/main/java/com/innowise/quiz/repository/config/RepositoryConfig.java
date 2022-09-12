package com.innowise.quiz.repository.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.innowise.quiz.repository")
public class RepositoryConfig {
}
