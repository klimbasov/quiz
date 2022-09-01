package com.innowise.quiz.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private  String name;

    @OneToMany(mappedBy = "quiz")
    private Set<Question> questions;

    @ManyToOne
    @JoinColumn(name = "group", nullable = false)
    private Group group;
}
