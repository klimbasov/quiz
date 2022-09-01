package com.innowise.quiz.domain.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Option {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "question", nullable = false)
    private Question question;
}
