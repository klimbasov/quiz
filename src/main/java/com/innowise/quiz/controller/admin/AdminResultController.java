package com.innowise.quiz.controller.admin;

import com.innowise.quiz.domain.dto.full.ResultDto;
import com.innowise.quiz.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/results")
@RequiredArgsConstructor
@Validated
public class AdminResultController {
    private final CrudService<ResultDto> service;

    @GetMapping(value = "/{id}")
    public ResultDto getById(@PathVariable @Min(1) Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/")
    public List<ResultDto> getAll(@RequestParam(required = false, defaultValue = "1") @Min(1) Integer page) {
        return service.getAll(page);
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultDto create(@RequestBody @Valid ResultDto dto) {
        return service.create(dto);
    }

    @PatchMapping(value = "/{id}")
    public ResultDto update(@RequestBody @Valid ResultDto dto, @PathVariable @Min(1) Long id) {
        return service.update(dto, id);
    }

    @DeleteMapping("/{id}")
    public ResultDto delete(@PathVariable @Min(1) Long id) {
        return service.delete(id);
    }
}
