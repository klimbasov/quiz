package com.innowise.quiz.controller.user;

import com.innowise.quiz.config.security.role.Role;
import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.entity.User;
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
@WithUserDetails("user")
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamControllerTest {
    private static final String BASE_URL = "/teams/";
    private static final String MOCK_USER_NAME = "user";
    private static final String MOCK_USER_PASSWORD = "password";
    private static final String PARAMETER_DELIMITER = "?";
    private static final String PAGE_PARAMETER = "page=";
    private static final String DELIMITER = "/";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    @PersistenceContext
    private EntityManager manager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private TeamMapper teamMapper;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserMapper userMapper;
    private Team persistentEntity;

    private static MockHttpServletRequestBuilder getJsonAccept(HttpMethod method, String url) {
        return request(method, url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @BeforeAll
    void setup() {
        initAndPersistAttached();
    }

    private void initAndPersistAttached() {
        UserDto leadDto = new UserDto()
                .setUsername(MOCK_USER_NAME)
                .setPassword(encoder.encode(MOCK_USER_PASSWORD))
                .setRoles(List.of(Role.USER.name()));
        User attachedUser = userMapper.toEntity(leadDto);
        executeTransactionConsumer(transactionStatus -> manager.persist(attachedUser));
    }

    @AfterAll
    void cleanupDb() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select t from Team t", Team.class).getResultStream().forEach(manager::remove);
            manager.createQuery("select u from User u", User.class).getResultStream().forEach(manager::remove);
            manager.flush();
        });
    }

    private Team createEntityWithGivenPrefix(int prefix) {
        return teamMapper.toEntity(new TeamDto()
                .setName("team name" + prefix));
    }

    private void executeTransactionConsumer(Consumer<TransactionStatus> consumer) {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(consumer);
    }

    private void persistSample(Team entity) {
        executeTransactionConsumer(transactionStatus -> manager.persist(entity));
    }

    @BeforeEach
    void backupDbToInitState() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select t from Team t", Team.class).getResultStream().forEach(manager::remove);
            manager.flush();
        });
    }

    @Test
    void getById() throws Exception {
        persistentEntity = createEntityWithGivenPrefix(20);
        persistSample(persistentEntity);
        long nonexistentId = persistentEntity.getId() + 1;
        long invalidId = -2;


        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + persistentEntity.getId())
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.name").value(persistentEntity.getName()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + nonexistentId)
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + invalidId)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + null)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void get() throws Exception {
        List<Team> quizzes = List.of(
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

    @Test
    void join() throws Exception {
        persistentEntity = createEntityWithGivenPrefix(1);
        persistSample(persistentEntity);
        long nonexistentId = persistentEntity.getId() + 1;
        long invalidId = -2;
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + persistentEntity.getId() + DELIMITER + "entrance")
                )
                .andExpect(status().is(HttpStatus.OK.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + persistentEntity.getId() + DELIMITER + "entrance")
                )
                .andExpect(status().is(HttpStatus.OK.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + nonexistentId + DELIMITER + "entrance")
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + invalidId + DELIMITER + "entrance")
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }
}