package com.innowise.quiz.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Question {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @OneToMany(mappedBy = "question")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Option> options;

    @ManyToOne
    @JoinColumn(name = "name", nullable = false)
    private Quiz quiz;
}
