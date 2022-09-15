package com.innowise.quiz.controller.teamlead;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("lead")
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeadTeamControllerTest {

    private static final String BASE_URL = "/leading/teams/";
    private static final String MOCK_USER_NAME = "lead";
    private static final String MOCK_USER_PASSWORD = "password";
    private static final String PARAMETER_DELIMITER = "?";
    private static final String PAGE_PARAMETER = "page=";
    private static final String DELIMITER = "/";

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
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserMapper userMapper;

    private TeamDto validDto;
    private TeamDto invalidContentDto;
    private TeamDto invalidIdDto;
    private TeamDto nonexistentDto;
    private Team persistentEntity;
    private User attachedTeamLead;

    private static MockHttpServletRequestBuilder getJsonAccept(HttpMethod method, String url) {
        return request(method, url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @BeforeAll
    void setup() {
        initAndPersistAttached();
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

    private void initAndPersistAttached() {
        UserDto leadDto = new UserDto()
                .setUsername(MOCK_USER_NAME)
                .setPassword(encoder.encode(MOCK_USER_PASSWORD))
                .setRoles(List.of(Role.LEAD.name()));
        attachedTeamLead = userMapper.toEntity(leadDto);
        executeTransactionConsumer(transactionStatus -> {
            manager.persist(attachedTeamLead);
        });
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
        executeTransactionConsumer(transactionStatus -> {
            User lead = manager.find(User.class, attachedTeamLead.getId());
            manager.persist(entity);
            entity.getLeads().add(lead);
            lead.getLeadingTeams().add(entity);
        });
    }

    @BeforeEach
    void backupDbToInitState() {
        executeTransactionConsumer(status -> {
            manager.createQuery("select t from Team t", Team.class).getResultStream().forEach(manager::remove);
            manager.find(User.class, attachedTeamLead.getId()).getLeadingTeams().clear();
            manager.flush();
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
    void patch() throws Exception {
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