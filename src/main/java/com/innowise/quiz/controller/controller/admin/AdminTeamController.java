package com.innowise.quiz.controller.controller.admin;

import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/teams")
@RequiredArgsConstructor
public class AdminTeamController {
    private final CrudService<TeamDto> service;

    @GetMapping(value = "/{id}")
    TeamDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/")
    List<TeamDto> getById(@RequestParam(required = false, defaultValue = "1") @Min(1) Integer page) {
        return service.getAll(page);
    }

    @PostMapping(value = "/")
    TeamDto create(@RequestBody TeamDto dto) {
        return service.create(dto);
    }

    @PatchMapping(value = "/")
    TeamDto update(@RequestBody TeamDto dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    TeamDto delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
