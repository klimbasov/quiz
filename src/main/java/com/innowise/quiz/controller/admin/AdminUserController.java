package com.innowise.quiz.controller.admin;

import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class AdminUserController {
    private final CrudService<UserDto> service;

    @GetMapping(value = "/{id}")
    public UserDto getById(@PathVariable @Min(1) Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/")
    public List<UserDto> get(@RequestParam(required = false, defaultValue = "1") @Min(1) Integer page) {
        return service.getAll(page);
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto dto) {
        return service.create(dto);
    }

    @PatchMapping(value = "/{id}")
    public UserDto update(@RequestBody @Valid UserDto dto, @PathVariable @Min(1) Long id) {
        return service.update(dto, id);
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable @Min(1) Long id) {
        return service.delete(id);
    }
}
