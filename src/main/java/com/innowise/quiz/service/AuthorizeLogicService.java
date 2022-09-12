package com.innowise.quiz.service;

public interface AuthorizeLogicService {
    void authorizeSelfUpdate(long userId, String credentialName);

    void authorizeTeamAccess(long teamId, String credentialName, boolean isLeadingAccessRequired);

    void authorizeQuizAccess(long quizId, String credentialName, boolean isLeadingAccessRequired);

    void authorizeQuizResultAccess(long quizResultId, String credentialName);
}
