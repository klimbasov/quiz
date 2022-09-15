package com.innowise.quiz.controller.admin;

import com.innowise.quiz.domain.dto.full.QuizDto;
import com.innowise.quiz.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/quizzes")
@RequiredArgsConstructor
@Validated
public class AdminQuizController {
    private final CrudService<QuizDto> service;

    @GetMapping(value = "/{id}")
    public QuizDto getById(@PathVariable @Min(1) Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/")
    public List<QuizDto> getAll(@RequestParam(required = false, defaultValue = "1") @Min(1) Integer page) {
        return service.getAll(page);
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizDto create(@RequestBody @Valid QuizDto dto) {
        return service.create(dto);
    }

    @PatchMapping(value = "/{id}")
    public QuizDto update(@RequestBody @Valid QuizDto dto, @PathVariable @Min(1) Long id) {
        return service.update(dto, id);
    }

    @DeleteMapping("/{id}")
    public QuizDto delete(@PathVariable @Min(1) Long id) {
        return service.delete(id);
    }
}
