package com.innowise.quiz.service.impl;

import com.innowise.quiz.domain.dto.full.QuizDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizDto;
import com.innowise.quiz.domain.entity.Quiz;
import com.innowise.quiz.domain.util.mapper.ext.QuizMapper;
import com.innowise.quiz.repository.QuizRepository;
import com.innowise.quiz.repository.TeamRepository;
import com.innowise.quiz.repository.UserRepository;
import com.innowise.quiz.service.config.ServicePropertySource;
import com.innowise.quiz.service.ext.QuizService;
import com.innowise.quiz.service.util.PaginationUtils;
import com.innowise.quiz.service.util.ThrowableLogicUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.innowise.quiz.service.util.ThrowableLogicUtils.getOrElseThrow;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {
    private final ServicePropertySource propertySource;
    private final QuizRepository repository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final QuizMapper mapper;

    @Override
    public QuizDto create(QuizDto dto) {
        Quiz rawEntity = mapper.toEntity(dto);
        rawEntity.getQuestions().forEach(question ->
                {
                    question.getOptions().forEach(option -> option.setQuestion(question));
                    question.setQuiz(rawEntity);
                }
        );
        rawEntity.setLead(getOrElseThrow(userRepository.findById(rawEntity.getLead().getId())));
        rawEntity.setTeam(getOrElseThrow(teamRepository.findById(rawEntity.getTeam().getId())));
        Quiz entity = repository.save(rawEntity);
        return mapper.toDto(entity);
    }

    @Override
    public QuizDto getById(Long id) {
        Quiz entity = getOrElseThrow(repository.findById(id));
        return mapper.toDto(entity);
    }

    @Override
    public List<SimpleQuizDto> getByName(String name, Integer page) {
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), "name");
        Page<Quiz> quizzes = repository.findQuizzesByNameContainingIgnoreCase(name, pageable);
        ThrowableLogicUtils.throwIfPageDoseNotExist(page, quizzes);
        return quizzes.map(mapper::toSimpleDto).getContent();
    }

    @Override
    public QuizDto delete(Long id) {
        QuizDto dto = mapper.toDto(getOrElseThrow(repository.findById(id)));
        repository.deleteById(id);
        return dto;
    }

    @Override
    public QuizDto update(QuizDto dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<QuizDto> getAll(Integer page) {
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), "name");
        Page<Quiz> quizzes = repository.findQuizzesByNameContainingIgnoreCase("", pageable);
        ThrowableLogicUtils.throwIfPageDoseNotExist(page, quizzes);
        return quizzes.map(mapper::toDto).getContent();
    }
}
