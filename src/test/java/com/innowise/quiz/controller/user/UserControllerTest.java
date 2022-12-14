package com.innowise.quiz.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.quiz.config.security.role.Role;
import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.domain.entity.User;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithUserDetails("user")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
    private static final String BASE_URL = "/users/";

    private static final String MOCK_USER_NAME = "user";
    private static final String MOCK_USER_PASSWORD = "password";
    //private static final String DELIMITER = "/";
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
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder encoder;
    private UserDto validDto;
    private UserDto selfUpdateDto;
    private UserDto invalidContentDto;
    private long invalidId;
    private long nonexistentId;
    private User attachedUser;
    private User persistentEntity;

    private static MockHttpServletRequestBuilder getJsonAccept(HttpMethod method, String url) {
        return request(method, url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @BeforeAll
    void setup() {
        persistAttached();
        nonexistentId = attachedUser.getId() + 1;
        invalidId = -2;
        validDto = new UserDto()
                .setUsername("user name")
                .setPassword("password")
                .setRoles(Arrays.asList(Role.ADMIN.name(), Role.USER.name()));
        invalidContentDto = new UserDto()
                .setPassword("password")
                .setRoles(Arrays.asList(Role.ADMIN.name(), Role.USER.name()));
    }

    private void persistAttached() {
        attachedUser = userMapper.toEntity(
                new UserDto()
                        .setUsername(MOCK_USER_NAME)
                        .setPassword(encoder.encode(MOCK_USER_PASSWORD))
                        .setRoles(Arrays.asList(Role.USER.name()))
        );
        executeTransactionConsumer(transactionStatus -> {
            manager.persist(attachedUser);
        });

    }

    @AfterAll
    void cleanupDb() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select  u from User u").getResultStream().forEach(manager::remove);
            manager.flush();
        });
    }

    @BeforeEach
    void backupBdToInitState() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select u from User u", User.class).getResultStream()
                    .filter(user -> !user.getId().equals(attachedUser.getId()))
                    .forEach(manager::remove);
            manager.flush();
        });
    }

    @Test
    @Order(2)
    void getUserById() throws Exception {
        persistentEntity = createEntityWithGivenPrefix(2);
        persistSample(persistentEntity);

        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + invalidId)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + persistentEntity.getId())
                )
                .andExpect(status().is(HttpStatus.OK.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + nonexistentId)
                )
                .andExpect(status().is(HttpStatus.OK.value()));
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
    @Order(3)
    void getSelf() throws Exception {
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + "self")
                )
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    @Order(1)
    void getUserByCriteria() throws Exception {
        List<User> users = new ArrayList<>();
        users.addAll(Arrays.asList(
                createEntityWithGivenPrefix(20),
                createEntityWithGivenPrefix(21),
                createEntityWithGivenPrefix(22)
        ));
        users.forEach(this::persistSample);
        users.add(attachedUser);
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL)
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$", hasSize(users.size())));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + PARAMETER_DELIMITER + PAGE_PARAMETER + 20)
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + PARAMETER_DELIMITER + PAGE_PARAMETER + (-23))
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @Order(4)
    void createUser() throws Exception {
        mockMvc.perform(
                        getJsonAccept(HttpMethod.POST, BASE_URL)
                                .content(objectMapper.writeValueAsString(validDto))
                )
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.username").value(validDto.getUsername()));

        //add one with repeatable username
        mockMvc.perform(
                        getJsonAccept(HttpMethod.POST, BASE_URL)
                                .content(objectMapper.writeValueAsString(validDto))
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.POST, BASE_URL)
                                .content("")
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.POST, BASE_URL)
                                .content(objectMapper.writeValueAsString(invalidContentDto))
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @Order(5)
    void updateSelf() throws Exception {
        selfUpdateDto = userMapper.toDto(attachedUser)
                .setPassword("new password");
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + "self")
                                .content(objectMapper.writeValueAsString(selfUpdateDto))
                )
                .andExpect(status().is(HttpStatus.OK.value()));
        cleanupDb();
        setup();
    }

    @Test
    @Order(6)
    void deleteSelf() throws Exception {
        mockMvc.perform(
                        getJsonAccept(HttpMethod.DELETE, BASE_URL + "self")
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.username").value(attachedUser.getUsername()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.DELETE, BASE_URL + "self")
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        cleanupDb();
        setup();
    }

    private User createEntityWithGivenPrefix(int prefix) {
        User user = new User();
        user.setRoles(Stream.of(Role.USER).collect(Collectors.toSet()));
        user.setUsername("username" + prefix);
        user.setPassword("password");
        user.setIsAccountNotLocked(true);
        return user;
    }

    private void executeTransactionConsumer(Consumer<TransactionStatus> consumer) {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(consumer);
    }

    private void persistSample(User entity) {
        executeTransactionConsumer(transactionStatus -> manager.persist(entity));
    }
}