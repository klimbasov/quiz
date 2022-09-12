package com.innowise.quiz.controller.controller.admin;

import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final CrudService<UserDto> service;

    @GetMapping(value = "/{id}")
    UserDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/")
    List<UserDto> getById(@RequestParam(required = false, defaultValue = "1") @Min(1) Integer page) {
        return service.getAll(page);
    }

    @PostMapping(value = "/")
    UserDto create(@RequestBody UserDto dto) {
        return service.create(dto);
    }

    @PatchMapping(value = "/")
    UserDto update(@RequestBody UserDto dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    UserDto delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
