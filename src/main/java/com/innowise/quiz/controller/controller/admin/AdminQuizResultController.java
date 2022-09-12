package com.innowise.quiz.controller.controller.admin;

import com.innowise.quiz.domain.dto.full.QuizResultDto;
import com.innowise.quiz.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/quizResults")
@RequiredArgsConstructor
public class AdminQuizResultController {
    private final CrudService<QuizResultDto> service;

    @GetMapping(value = "/{id}")
    QuizResultDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/")
    List<QuizResultDto> getById(@RequestParam(required = false, defaultValue = "1") @Min(1) Integer page) {
        return service.getAll(page);
    }

    @PostMapping(value = "/")
    QuizResultDto create(@RequestBody QuizResultDto dto) {
        return service.create(dto);
    }

    @PatchMapping(value = "/")
    QuizResultDto update(@RequestBody QuizResultDto dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    QuizResultDto delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
