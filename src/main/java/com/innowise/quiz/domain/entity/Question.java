package com.innowise.quiz.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "questions")
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Set<Option> options = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "quiz", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Quiz quiz;
}
