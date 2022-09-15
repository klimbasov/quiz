package com.innowise.quiz.service.impl;

import com.innowise.quiz.config.ServicePropertySource;
import com.innowise.quiz.domain.dto.full.QuizDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizDto;
import com.innowise.quiz.domain.entity.Question;
import com.innowise.quiz.domain.entity.Quiz;
import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.utill.mapper.QuestionMapper;
import com.innowise.quiz.domain.utill.mapper.QuizMapper;
import com.innowise.quiz.repository.QuizRepository;
import com.innowise.quiz.repository.TeamRepository;
import com.innowise.quiz.service.QuizService;
import com.innowise.quiz.service.utill.PaginationUtils;
import com.innowise.quiz.service.utill.ThrowableLogicUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.innowise.quiz.service.utill.ThrowableLogicUtils.getOrElseThrow;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {
    private static final String SORTING_PROPERTY_NAME = "name";
    private final ServicePropertySource propertySource;
    private final QuizRepository repository;
    private final TeamRepository teamRepository;

    private final QuestionMapper questionMapper;
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
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), SORTING_PROPERTY_NAME);
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
    public QuizDto update(@Valid QuizDto dto, Long id) {
        Quiz entity = getOrElseThrow(repository.findById(id));
        mapper.update(dto, entity);
        mapRelations(dto, entity);
        return mapper.toDto(entity);
    }

    private void mapRelations(QuizDto dto, Quiz entity) {
        if (nonNull(dto.getQuestions())) {
            Set<Question> questions = dto.getQuestions().stream()
                    .map(questionDto -> {
                        Question question = questionMapper.toEntity(questionDto);
                        question.setQuiz(entity);
                        return question;
                    })
                    .collect(Collectors.toSet());
            entity.setQuestions(questions);
        }
        if (nonNull(dto.getTeam())) {
            Team team = getOrElseThrow(teamRepository.findById(dto.getTeam().getId()));
            entity.setTeam(team);
        }
    }

    @Override
    public List<QuizDto> getAll(Integer page) {
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), SORTING_PROPERTY_NAME);
        Page<Quiz> quizzes = repository.findQuizzesByNameContainingIgnoreCase("", pageable);
        ThrowableLogicUtils.throwIfPageDoseNotExist(page, quizzes);
        return quizzes.map(mapper::toDto).getContent();
    }
}
