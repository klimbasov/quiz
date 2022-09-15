package com.innowise.quiz.controller.teamlead;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.quiz.config.security.role.Role;
import com.innowise.quiz.domain.dto.full.*;
import com.innowise.quiz.domain.entity.Quiz;
import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.domain.utill.mapper.QuizMapper;
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
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("lead")
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeadQuizControllerTest {
    private static final String BASE_URL = "/leading/quizzes/";

    private static final String MOCK_USER_NAME = "lead";
    private static final String MOCK_USER_PASSWORD = "password";
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
    private TeamMapper teamMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder encoder;
    private QuizDto validDto;
    private QuizDto invalidContentDto;
    private QuizDto invalidIdDto;
    private QuizDto nonexistentDto;
    private Team attachedTeam;
    private QuestionDto attachedQuestionDto;
    private Quiz persistentEntity;

    private static MockHttpServletRequestBuilder getJsonAccept(HttpMethod method, String url) {
        return request(method, url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @BeforeAll
    void setup() {
        initAndPersistAttached();
        initAttachedQuestion();

        validDto = new QuizDto()
                .setName("name")
                .setQuestions(List.of(attachedQuestionDto))
                .setTeam(teamMapper.toSimpleDto(attachedTeam));
        invalidContentDto = new QuizDto()
                .setName("")
                .setQuestions(List.of(attachedQuestionDto))
                .setTeam(teamMapper.toSimpleDto(attachedTeam));
        invalidIdDto = new QuizDto()
                .setId(-2)
                .setName("name")
                .setQuestions(List.of(attachedQuestionDto))
                .setTeam(teamMapper.toSimpleDto(attachedTeam));
        nonexistentDto = new QuizDto()
                .setId(20)
                .setName("name")
                .setQuestions(List.of(attachedQuestionDto))
                .setTeam(teamMapper.toSimpleDto(attachedTeam));
    }

    @AfterAll
    void cleanupDb() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select q from Quiz q", Quiz.class).getResultStream().forEach(manager::remove);
            manager.createQuery("select t from Team t", Team.class).getResultStream().forEach(manager::remove);
            manager.createQuery("select u from User u", User.class).getResultStream().forEach(manager::remove);
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
        TeamDto attachedTeamDto = new TeamDto()
                .setName("team name");
        User attachedTeamLead = userMapper.toEntity(leadDto);
        attachedTeam = teamMapper.toEntity(attachedTeamDto);
        executeTransactionConsumer(transactionStatus -> {
            manager.persist(attachedTeam);
            manager.persist(attachedTeamLead);
            attachedTeam.getLeads().add(attachedTeamLead);
            attachedTeamLead.getLeadingTeams().add(attachedTeam);
        });
    }

    @BeforeEach
    void initDbState() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select q from Quiz q", Quiz.class).getResultStream().forEach(manager::remove);
            manager.createQuery("select t from Team t", Team.class).getResultStream().forEach(team -> team.getQuizzes().clear());
            manager.flush();
        });
    }

    private Quiz createEntityWithGivenPrefix(int prefix) {
        return quizMapper.toEntity(new QuizDto()
                .setName("name" + prefix)
                .setQuestions(List.of(attachedQuestionDto))
                .setTeam(teamMapper.toSimpleDto(attachedTeam)));
    }

    private void executeTransactionConsumer(Consumer<TransactionStatus> consumer) {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(consumer);
    }

    private void persistSample(Quiz entity) {
        executeTransactionConsumer(transactionStatus -> {
            Team team = manager.find(Team.class, attachedTeam.getId());
            team.getQuizzes().add(entity);
            entity.setTeam(team);
            manager.persist(entity);
        });
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(
                        getJsonAccept(HttpMethod.POST, BASE_URL)
                                .content(objectMapper.writeValueAsString(validDto))
                )
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.name").value(validDto.getName()));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.POST, BASE_URL)
                                .content(objectMapper.writeValueAsString(validDto))
                )
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.name").value(validDto.getName()));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.POST, BASE_URL)
                                .content(objectMapper.writeValueAsString(invalidContentDto))
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.POST, BASE_URL)
                                .content("")
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void patch() throws Exception {
        persistentEntity = createEntityWithGivenPrefix(1);
        persistSample(persistentEntity);
        QuizDto updatedDto = quizMapper.toDto(persistentEntity)
                .setName("newName");
        updatedDto.getQuestions().remove(0);
        QuizDto invalidUpdatedDto = quizMapper.toDto(persistentEntity)
                .setName("");
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + persistentEntity.getId())
                                .content(objectMapper.writeValueAsString(updatedDto))
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.id").value(persistentEntity.getId()))
                .andExpect(jsonPath("$.name").value(updatedDto.getName()))
                .andExpect(jsonPath("$.questions", hasSize(not(persistentEntity.getQuestions().size()))));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + nonexistentDto.getId())
                                .content(objectMapper.writeValueAsString(updatedDto))
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + nonexistentDto.getId())
                                .content(objectMapper.writeValueAsString(updatedDto))
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + persistentEntity.getId())
                                .content(objectMapper.writeValueAsString(invalidUpdatedDto))
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void delete() throws Exception {
        persistentEntity = createEntityWithGivenPrefix(1);
        persistSample(persistentEntity);
        mockMvc.perform(
                        getJsonAccept(HttpMethod.DELETE, BASE_URL + persistentEntity.getId())
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.name").value(persistentEntity.getName()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.DELETE, BASE_URL + persistentEntity.getId())
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.DELETE, BASE_URL + invalidIdDto.getId())
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.DELETE, BASE_URL + null)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }
}