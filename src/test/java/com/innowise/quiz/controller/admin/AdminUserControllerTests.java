package com.innowise.quiz.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.quiz.config.security.role.Role;
import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.domain.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
@WithMockUser(roles = {"ADMIN"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminUserControllerTests {
    private static final String BASE_URL = "/admin/users/";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired    //todo move to EntityManager
    @PersistenceContext
    private EntityManager manager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private UserDto validDto;
    private UserDto invalidContentDto;
    private UserDto invalidIdDto;
    private UserDto nonexistentDto;
    private User persistentEntity;

    private static MockHttpServletRequestBuilder getJsonAccept(HttpMethod method, String url) {
        return request(method, url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @BeforeAll
    void setup() {
        persistentEntity = createEntityWithGivenPrefix(20);

        validDto = new UserDto()
                .setUsername("name")
                .setPassword("password")
                .setRoles(Arrays.asList(Role.ADMIN.name(), Role.USER.name()));
        invalidContentDto = new UserDto()
                .setUsername("")
                .setPassword("password")
                .setRoles(Arrays.asList(Role.ADMIN.name(), Role.USER.name()));
        nonexistentDto = new UserDto()
                .setId(13)
                .setUsername("name")
                .setPassword("password")
                .setRoles(Arrays.asList(Role.ADMIN.name(), Role.USER.name()));
        invalidIdDto = new UserDto()
                .setId(-2)
                .setUsername("name")
                .setPassword("password")
                .setRoles(Arrays.asList(Role.ADMIN.name(), Role.USER.name()));
    }

    @AfterAll
    void cleanupDb() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select u from User u", User.class).getResultStream().forEach(manager::remove);
            manager.flush();
        });
    }

    @BeforeEach
    void backupBdToInitState() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select u from User u", User.class).getResultStream().forEach(manager::remove);
            manager.flush();
        });
    }

    @Test
    void getById() throws Exception {
        persistentEntity = createEntityWithGivenPrefix(23);
        persistSample(persistentEntity);

        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + invalidIdDto.getId())
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + persistentEntity.getId())
                )
                .andExpect(status().is(HttpStatus.OK.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + nonexistentDto.getId())
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + invalidIdDto.getId())
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + null)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void getAll() throws Exception {
        List<User> users = List.of(
                createEntityWithGivenPrefix(20),
                createEntityWithGivenPrefix(21),
                createEntityWithGivenPrefix(22)
        );
        users.forEach(this::persistSample);
        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL)
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$", hasSize(users.size())));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, "/admin/users/?page=2")
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, "/admin/users/?page=-1")
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void create() throws Exception {

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
    void update() throws Exception {
        persistentEntity = createEntityWithGivenPrefix(23);
        persistSample(persistentEntity);
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + persistentEntity.getId())
                                .content(objectMapper.writeValueAsString(validDto))
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.username").value(validDto.getUsername()));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + persistentEntity.getId())
                                .content(objectMapper.writeValueAsString(invalidContentDto))
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + nonexistentDto.getId())
                                .content(objectMapper.writeValueAsString(nonexistentDto))
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + invalidIdDto.getId())
                                .content(objectMapper.writeValueAsString(invalidIdDto))
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void delete() throws Exception {
        persistSample(persistentEntity);
        mockMvc.perform(
                        getJsonAccept(HttpMethod.DELETE, BASE_URL + persistentEntity.getId())
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.username").value(persistentEntity.getUsername()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.DELETE, BASE_URL + persistentEntity.getId())
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        mockMvc.perform(
                        getJsonAccept(HttpMethod.DELETE, BASE_URL + invalidIdDto.getId())
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
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
