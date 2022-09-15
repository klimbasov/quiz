package com.innowise.quiz.service.impl;

import com.innowise.quiz.config.ServicePropertySource;
import com.innowise.quiz.domain.dto.full.ResultDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.domain.entity.Quiz;
import com.innowise.quiz.domain.entity.Result;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.domain.utill.mapper.ResultMapper;
import com.innowise.quiz.repository.QuizRepository;
import com.innowise.quiz.repository.ResultRepository;
import com.innowise.quiz.repository.UserRepository;
import com.innowise.quiz.service.ResultService;
import com.innowise.quiz.service.utill.ThrowableLogicUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

import static com.innowise.quiz.service.utill.PaginationUtils.createPageable;
import static com.innowise.quiz.service.utill.ThrowableLogicUtils.getOrElseThrow;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Transactional
public class ResultServiceImpl implements ResultService {
    private static final String SORTING_PROPERTY_NAME = "name";
    private final ServicePropertySource propertySource;
    private final ResultRepository repository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final ResultMapper mapper;

    @Override
    public ResultDto create(ResultDto dto) {
        Result rawEntity = mapper.toEntity(dto);
        rawEntity.setQuiz(getOrElseThrow(quizRepository.findById(dto.getQuiz().getId())));
        rawEntity.setUser(getOrElseThrow(userRepository.findById(dto.getUser().getId())));
        Result entity = repository.save(rawEntity);
        return mapper.toDto(entity);
    }

    @Override
    public ResultDto getById(Long id) {
        Result entity = getOrElseThrow(repository.findById(id));
        return mapper.toDto(entity);
    }

    @Override
    public List<SimpleQuizResultDto> getByUsernameAndTeamIdAndByQuizPartialName(
            String username,
            String quizName,
            Integer page) {
        User user = getOrElseThrow(userRepository.findUsersByUsername(username));
        Pageable pageable = createPageable(page, propertySource.getPageSize(), SORTING_PROPERTY_NAME);
        Page<Result> results = repository.findByUserAndQuizNameContains(user, quizName, pageable);
        ThrowableLogicUtils.throwIfPageDoseNotExist(page, results);
        return results.map(mapper::toSimpleDto).getContent();
    }

    @Override
    public List<SimpleQuizResultDto> getByLeadNameAndByQuizId(String leadName, Long quizId, Integer page) {
        User lead = getOrElseThrow(userRepository.findUsersByUsername(leadName));
        Pageable pageable = createPageable(page, propertySource.getPageSize(), SORTING_PROPERTY_NAME);
        Page<Result> results = repository.findByQuizId(quizId, lead, pageable);
        ThrowableLogicUtils.throwIfPageDoseNotExist(page, results);
        return results.map(mapper::toSimpleDto).getContent();
    }

    @Override
    public ResultDto delete(Long id) {
        Result entity = getOrElseThrow(repository.findById(id));
        entity.getUser().getResults().remove(entity);
        entity.getQuiz().getResults().remove(entity);
        repository.delete(entity);
        return mapper.toDto(entity);
    }

    @Override
    public ResultDto update(@Valid ResultDto dto, Long id) {
        Result entity = getOrElseThrow(repository.findById(id));
        mapper.update(dto, entity);
        mapRelations(dto, entity);
        return mapper.toDto(entity);
    }

    @Override
    public List<ResultDto> getAll(Integer page) {
        Pageable pageable = createPageable(page, propertySource.getPageSize(), "name");
        Page<Result> results = repository.findAll(pageable);
        ThrowableLogicUtils.throwIfPageDoseNotExist(page, results);
        return results.map(mapper::toDto).getContent();
    }

    private void mapRelations(ResultDto dto, Result entity) {
        if (nonNull(dto.getUser())) {
            User user = getOrElseThrow(userRepository.findById(dto.getUser().getId()));
            entity.setUser(user);
        }
        if (nonNull(dto.getQuiz())) {
            Quiz quiz = getOrElseThrow(quizRepository.findById(dto.getQuiz().getId()));
            entity.setQuiz(quiz);
        }
    }
}
