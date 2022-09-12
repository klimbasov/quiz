package com.innowise.quiz.service.impl;

import com.innowise.quiz.domain.dto.full.QuizResultDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.domain.entity.QuizResult;
import com.innowise.quiz.domain.util.mapper.ext.QuizResultMapper;
import com.innowise.quiz.repository.QuizRepository;
import com.innowise.quiz.repository.QuizResultRepository;
import com.innowise.quiz.repository.TeamRepository;
import com.innowise.quiz.repository.UserRepository;
import com.innowise.quiz.service.config.ServicePropertySource;
import com.innowise.quiz.service.ext.QuizResultService;
import com.innowise.quiz.service.util.PaginationUtils;
import com.innowise.quiz.service.util.ThrowableLogicUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.innowise.quiz.service.util.PaginationUtils.createPageable;
import static com.innowise.quiz.service.util.ThrowableLogicUtils.getOrElseThrow;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizResultServiceImpl implements QuizResultService {
    private final ServicePropertySource propertySource;
    private final QuizResultRepository repository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final QuizResultMapper mapper;

    @Override
    public QuizResultDto create(QuizResultDto dto) {
        QuizResult rawEntity = mapper.toEntity(dto);
        rawEntity.setQuiz(getOrElseThrow(quizRepository.findById(rawEntity.getQuiz().getId())));
        rawEntity.setTeam(getOrElseThrow(teamRepository.findById(rawEntity.getTeam().getId())));
        rawEntity.setUser(getOrElseThrow(userRepository.findById(rawEntity.getUser().getId())));
        QuizResult entity = repository.save(rawEntity);
        return mapper.toDto(entity);
    }

    @Override
    public QuizResultDto getById(Long id) {
        QuizResult entity = getOrElseThrow(repository.findById(id));
        return mapper.toDto(entity);
    }

    @Override
    public List<SimpleQuizResultDto> getByUsernameAndTeamIdAndByQuizPartialName(
            String username,
            String quizName,
            Integer page) {
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), "name");
        Page<QuizResult> results = repository.findByName(
                username,
                quizName,
                pageable);
        ThrowableLogicUtils.throwIfPageDoseNotExist(page, results);
        return results.map(mapper::toSimpleDto).getContent();
    }

    @Override
    public List<SimpleQuizResultDto> getByLeadNameAndByQuizId(
            String leadName,
            Long quizId,
            Integer page) {
        Pageable pageable = createPageable(page, propertySource.getPageSize(), "name");
        Page<QuizResult> results = repository.findByQuizId(
                quizId,
                leadName,
                pageable);
        ThrowableLogicUtils.throwIfPageDoseNotExist(page, results);
        return results.map(mapper::toSimpleDto).getContent();
    }

    @Override
    public QuizResultDto delete(Long id) {
        QuizResultDto dto = mapper.toDto(getOrElseThrow(repository.findById(id)));
        repository.deleteById(id);
        return dto;
    }

    @Override
    public QuizResultDto update(QuizResultDto dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<QuizResultDto> getAll(Integer page) {
        Pageable pageable = createPageable(page, propertySource.getPageSize(), "name");
        Page<QuizResult> results = repository.findByQuizId(
                null,
                null,
                pageable);
        ThrowableLogicUtils.throwIfPageDoseNotExist(page, results);
        return results.map(mapper::toDto).getContent();
    }
}
