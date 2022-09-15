package com.innowise.quiz.service;

import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.repository.QuizRepository;
import com.innowise.quiz.repository.ResultRepository;
import com.innowise.quiz.repository.TeamRepository;
import com.innowise.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.innowise.quiz.service.utill.ThrowableLogicUtils.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorizeLogicService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final QuizRepository quizRepository;
    private final ResultRepository resultRepository;

    public void authorizeTeamAccess(long teamId, String credentialName, boolean isLeadingAccessRequired) {
        Team team = getOrElseThrow(teamRepository.findById(teamId));
        authorizeTeamAccessInternal(credentialName, isLeadingAccessRequired, team);
    }

    public void authorizeQuizAccess(long quizId, String credentialName, boolean isLeadingAccessRequired) {
        Team team = getOrElseThrow(quizRepository.findById(quizId)).getTeam();
        authorizeTeamAccessInternal(credentialName, isLeadingAccessRequired, team);

    }

    public void authorizeQuizResultAccess(long quizResultId, String credentialName) {
        Team team = getOrElseThrow(resultRepository.findById(quizResultId)).getQuiz().getTeam();
        authorizeTeamAccessInternal(credentialName, false, team);
    }

    private User getUserByCredentialName(String credentialName) {
        return getOrElseThrow(userRepository.findUsersByUsername(credentialName));
    }

    private void authorizeTeamAccessInternal(String credentialName, boolean isLeadingAccessRequired, Team team) {
        User user = getUserByCredentialName(credentialName);
        if (isLeadingAccessRequired) {
            throwIfIsNotLeadOfTeam(team, user);
        } else {
            throwIfIsNotUserOfTeam(team, user);
        }
    }
}
