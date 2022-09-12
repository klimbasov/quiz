package com.innowise.quiz.controller.controller.user;

import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;
import com.innowise.quiz.service.AuthorizeLogicService;
import com.innowise.quiz.service.ext.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthorizeLogicService authorizeLogicService;

    @GetMapping(value = "/{id}")
    UserDto getUserById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/self")
    UserDto getUserById(Principal principal) {
        return service.getByStrictName(principal.getName());
    }

    @GetMapping(value = "/")
    List<SimpleUserDto> getUserByCriteria(
            @RequestParam(required = false, defaultValue = "") String partialName,
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page
    ) {
        return service.getByName(partialName, page);
    }

    @PostMapping(value = "/")
    UserDto createUser(@RequestBody UserDto dto) {
        return service.create(dto);
    }

    @PatchMapping(value = "/self")
    UserDto updateSelf(@RequestBody UserDto dto, Principal principal) {
        authorizeLogicService.authorizeSelfUpdate(dto.getId(), principal.getName());
        return service.updateSelf(dto);
    }

    @DeleteMapping(value = "/self")
    UserDto delete(Principal principal) {
        return service.deleteSelf(principal.getName());
    }
}
