package com.innowise.quiz.controller.user;

import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;
import com.innowise.quiz.service.UserService;
import com.innowise.quiz.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService service;

    @GetMapping(value = "/{id}")
    public UserDto getUserById(@PathVariable @Min(1) Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/self")
    public UserDto getSelf(Principal principal) {
        return service.getByStrictName(principal.getName());
    }

    @GetMapping(value = "/")
    public List<SimpleUserDto> getUserByCriteria(
            @RequestParam(required = false, defaultValue = "") String partialName,
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page
    ) {
        return service.getByName(partialName, page);
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto dto) {
        return service.create(dto);
    }

    @PatchMapping(value = "/self")
    public UserDto updateSelf(@RequestBody @Valid UserDto dto, Principal principal) {
        UserDto authDto = service.getByStrictName(principal.getName());
        if (!authDto.getUsername().equals(principal.getName())) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED);
        }
        return service.updateSelf(dto, authDto.getId());
    }

    @DeleteMapping(value = "/self")
    public UserDto deleteSelf(Principal principal) {
        return service.deleteSelf(principal.getName());
    }
}
