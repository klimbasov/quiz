package com.innowise.quiz.controller.controller.admin;

import com.innowise.quiz.domain.dto.full.QuizDto;
import com.innowise.quiz.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/quizzes")
@RequiredArgsConstructor
public class AdminQuizController {
    private final CrudService<QuizDto> service;

    @GetMapping(value = "/{id}")
    QuizDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/")
    List<QuizDto> getById(@RequestParam(required = false, defaultValue = "1") @Min(1) Integer page) {
        return service.getAll(page);
    }

    @PostMapping(value = "/")
    QuizDto create(@RequestBody QuizDto dto) {
        return service.create(dto);
    }

    @PatchMapping(value = "/")
    QuizDto update(@RequestBody QuizDto dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    QuizDto delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
