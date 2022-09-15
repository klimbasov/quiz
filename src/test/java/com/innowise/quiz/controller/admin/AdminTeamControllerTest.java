package com.innowise.quiz.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.utill.mapper.TeamMapper;
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
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = {"ADMIN"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminTeamControllerTest {
    private static final String BASE_URL = "/admin/teams/";
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
    private TeamMapper teamMapper;

    private TeamDto validDto;
    private TeamDto invalidContentDto;
    private TeamDto invalidIdDto;
    private TeamDto nonexistentDto;
    private Team persistentEntity;

    private static MockHttpServletRequestBuilder getJsonAccept(HttpMethod method, String url) {
        return request(method, url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @BeforeAll
    void setup() {
        validDto = new TeamDto()
                .setName("team name");
        invalidContentDto = new TeamDto()
                .setName("");
        invalidIdDto = new TeamDto()
                .setId(-2)
                .setName("team name");
        nonexistentDto = new TeamDto()
                .setId(20)
                .setName("team name");
    }

    @AfterAll
    void cleanupDb() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select t from Team t", Team.class).getResultStream().forEach(manager::remove);
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
        executeTransactionConsumer(transactionStatus -> {
            manager.persist(entity);
        });
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

        mockMvc.perform(
                        getJsonAccept(HttpMethod.GET, BASE_URL + persistentEntity.getId())
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.name").value(persistentEntity.getName()));
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
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

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
    void update() throws Exception {
        persistentEntity = createEntityWithGivenPrefix(1);
        persistSample(persistentEntity);
        TeamDto updatedDto = teamMapper.toDto(persistentEntity)
                .setName("newName");
        TeamDto invalidUpdatedDto = teamMapper.toDto(persistentEntity)
                .setName("");
        mockMvc.perform(
                        getJsonAccept(HttpMethod.PATCH, BASE_URL + persistentEntity.getId())
                                .content(objectMapper.writeValueAsString(updatedDto))
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.id").value(persistentEntity.getId()))
                .andExpect(jsonPath("$.name").value(updatedDto.getName()));
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