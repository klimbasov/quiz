package com.innowise.quiz.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "team")
@Data
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "team")
    private Set<Quiz> quizzes = new HashSet<>();

    @OneToMany(mappedBy = "team")
    private Set<QuizResult> results = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "group_leads",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "leads_id")
    )
    private Set<User> leads = new HashSet<>();

    public void addUser(User user) {
        this.users.add(user);
        user.getTeams().add(this);
    }

    public void addLead(User user) {
        this.leads.add(user);
        user.getLeadingTeams().add(this);
    }
}
