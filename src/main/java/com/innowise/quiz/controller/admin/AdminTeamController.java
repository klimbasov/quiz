package com.innowise.quiz.controller.admin;

import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/teams")
@RequiredArgsConstructor
@Validated
public class AdminTeamController {
    private final CrudService<TeamDto> service;

    @GetMapping(value = "/{id}")
    public TeamDto getById(@PathVariable @Min(1) Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/")
    public List<TeamDto> getById(@RequestParam(required = false, defaultValue = "1") @Min(1) Integer page) {
        return service.getAll(page);
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamDto create(@RequestBody @Valid TeamDto dto) {
        return service.create(dto);
    }

    @PatchMapping(value = "/{id}")
    public TeamDto update(@RequestBody @Valid TeamDto dto, @PathVariable @Min(1) Long id) {
        return service.update(dto, id);
    }

    @DeleteMapping("/{id}")
    public TeamDto delete(@PathVariable @Min(1) Long id) {
        return service.delete(id);
    }
}
