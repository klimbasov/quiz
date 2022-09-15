package com.innowise.quiz.controller.teamlead;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.quiz.config.security.role.Role;
import com.innowise.quiz.domain.dto.full.*;
import com.innowise.quiz.domain.entity.Quiz;
import com.innowise.quiz.domain.entity.Result;
import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.domain.utill.mapper.QuizMapper;
import com.innowise.quiz.domain.utill.mapper.ResultMapper;
import com.innowise.quiz.domain.utill.mapper.TeamMapper;
import com.innowise.quiz.domain.utill.mapper.UserMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("lead")
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeadQuizResultControllerTest {

    private static final String BASE_URL = "/leading/results/";
    private static final String MOCK_USER_NAME = "lead";
    private static final String MOCK_USER_PASSWORD = "password";
    private static final String PARAMETER_DELIMITER = "?";
    private static final String PAGE_PARAMETER = "page=";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @PersistenceContext
    private EntityManager manager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private QuizMapper quizMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TeamMapper teamMapper;
    @Autowired
    private ResultMapper resultMapper;
    @Autowired
    private PasswordEncoder encoder;
    private Quiz attachedQuiz;
    private User attachedUser;
    private QuestionDto attachedQuestionDto;
    private Long nonexistentQuizId;
    private Long invalidQuizId;
    private Result persistentEntity;

    private static MockHttpServletRequestBuilder getJsonAccept(HttpMethod method, String url) {
        return request(method, url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @BeforeAll
    void setup() {
        initAttachedQuestion();
        initAndPersistAttached();
        nonexistentQuizId = attachedQuiz.getId() + 1;
        invalidQuizId = -2L;
    }

    @AfterAll
    void cleanupDb() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select r from Result r", Result.class).getResultStream().forEach(result -> {
                result.getQuiz().getResults().remove(result);
                result.getUser().getResults().remove(result);
                manager.remove(result);
            });
            manager.createQuery("select q from Quiz q", Quiz.class).getResultStream().forEach(quiz -> {
                quiz.getResults().clear();
                quiz.getTeam().getQuizzes().remove(quiz);
                manager.remove(quiz);
            });
            manager.createQuery("select u from User u", User.class).getResultStream().forEach(user -> {
                user.getResults().clear();
                user.getTeams().clear();
                manager.remove(user);
            });
            manager.createQuery("select t from Team t", Team.class).getResultStream().forEach(manager::remove);
            manager.flush();
        });
    }

    private void initAttachedQuestion() {
        List<OptionDto> attachedOptionDtos = List.of(
                new OptionDto()
                        .setText("correct option")
                        .setIsCorrect(true),
                new OptionDto()
                        .setText("not correct option")
                        .setIsCorrect(true)
        );
        attachedQuestionDto = new QuestionDto()
                .setText("some text")
                .setOptions(attachedOptionDtos);

    }

    private void initAndPersistAttached() {
        UserDto leadDto = new UserDto()
                .setUsername(MOCK_USER_NAME)
                .setPassword(encoder.encode(MOCK_USER_PASSWORD))
                .setRoles(List.of(Role.LEAD.name()));
        User attachedTeamLead = userMapper.toEntity(leadDto);
        TeamDto attachedTeamDto = new TeamDto()
                .setName("team name");
        Team attachedTeam = teamMapper.toEntity(attachedTeamDto);
        QuizDto attachedQuizDto = new QuizDto()
                .setName("quiz name")
                .setQuestions(List.of(attachedQuestionDto));
        attachedQuiz = quizMapper.toEntity(attachedQuizDto);
        UserDto attachedUserDto = new UserDto()
                .setUsername("user name")
                .setPassword("password")
                .setRoles(List.of(Role.ADMIN.name(), Role.USER.name(), Role.LEAD.name()))
                .setIsAccountNotLocked(true);
        attachedUser = userMapper.toEntity(attachedUserDto);
        executeTransactionConsumer(transactionStatus -> {
            manager.persist(attachedTeam);
            attachedQuiz.setTeam(attachedTeam);
            manager.persist(attachedQuiz);
            attachedTeam.getQuizzes().add(attachedQuiz);
            manager.persist(attachedUser);
            attachedUser.getTeams().add(attachedTeam);
            manager.persist(attachedTeamLead);
            attachedTeamLead.getLeadingTeams().add(attachedTeam);
            attachedTeam.getLeads().add(attachedTeamLead);
        });

    }

    private Result createEntityWithGivenPrefix(int prefix) {
        return resultMapper.toEntity(new ResultDto()
                .setName("name" + prefix)
                .setResult(0.1f)
                .setQuiz(quizMapper.toSimpleDto(attachedQuiz))
                .setUser(userMapper.toSimpleDto(attachedUser)));
    }

    private void executeTransactionConsumer(Consumer<TransactionStatus> consumer) {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(consumer);
    }

    private void persistSample(Result entity) {
        executeTransactionConsumer(transactionStatus -> {
            Quiz quiz = manager.find(Quiz.class, attachedQuiz.getId());
            User user = manager.find(User.class, attachedUser.getId());
            quiz.getResults().add(entity);
            user.getResults().add(entity);
            entity.setUser(user);
            entity.setQuiz(quiz);
            manager.persist(entity);
        });
    }

    @BeforeEach
    void initDbState() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select q from Quiz q", Quiz.class).getResultStream().forEach(team -> team.getResults().clear());
            manager.createQuery("select u from User u", User.class).getResultStream().forEach(team -> team.getResults().clear());
            manager.createQuery("select r from Result r", Result.class).getResultStream().forEach(manager::remove);
            manager.flush();
        });
    }

    @Test
    void getByQuizId() throws Exception {
        persistentEntity = createEntityWithGivenPrefix(20);
        persistSample(persistentEntity);

        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + attachedQuiz.getId())
                )
                .andExpect(status().is(HttpStatus.OK.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + nonexistentQuizId)//todo refactoring
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + invalidQuizId)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + null)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void get() throws Exception {
        List<Result> quizzes = List.of(
                createEntityWithGivenPrefix(21),
                createEntityWithGivenPrefix(22),
                createEntityWithGivenPrefix(23)
        );
        quizzes.forEach(this::persistSample);
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL)
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$", hasSize(quizzes.size())));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + PARAMETER_DELIMITER + PAGE_PARAMETER + 1)
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$", hasSize(quizzes.size())));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + PARAMETER_DELIMITER + PAGE_PARAMETER + 10)
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + PARAMETER_DELIMITER + PAGE_PARAMETER + (-2))
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + PARAMETER_DELIMITER + PAGE_PARAMETER + null)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }
}