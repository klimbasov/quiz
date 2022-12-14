package com.innowise.quiz.service.impl;

import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.repository.QuizRepository;
import com.innowise.quiz.repository.QuizResultRepository;
import com.innowise.quiz.repository.TeamRepository;
import com.innowise.quiz.repository.UserRepository;
import com.innowise.quiz.service.AuthorizeLogicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.innowise.quiz.service.util.ThrowableLogicUtils.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorizeLogicServiceImpl implements AuthorizeLogicService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final QuizRepository quizRepository;
    private final QuizResultRepository quizResultRepository;

    @Override
    public void authorizeSelfUpdate(long userId, String credentialName) {
        User expected = getUserByCredentialName(credentialName);
        User actual = getOrElseThrow(userRepository.findById(userId));
        throwIfSelfAccessDenied(expected, actual);
    }

    @Override
    public void authorizeTeamAccess(long teamId, String credentialName, boolean isLeadingAccessRequired) {
        Team team = getOrElseThrow(teamRepository.findById(teamId));
        authorizeTeamAccessInternal(credentialName, isLeadingAccessRequired, team);
    }

    @Override
    public void authorizeQuizAccess(long quizId, String credentialName, boolean isLeadingAccessRequired) {
        Team team = getOrElseThrow(quizRepository.findById(quizId)).getTeam();
        authorizeTeamAccessInternal(credentialName, isLeadingAccessRequired, team);

    }

    @Override
    public void authorizeQuizResultAccess(long quizResultId, String credentialName) {
        Team team = getOrElseThrow(quizResultRepository.findById(quizResultId)).getTeam();
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
