package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/users", "/api/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse create(@RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{id}")
    public UserResponse read(@PathVariable UUID id) {
        return userService.read(id);
    }

    @GetMapping
    public List<UserResponse> readAll() {
        return userService.readAll();
    }

    // 심화 요구사항: GET /api/user/findAll
    @GetMapping("/findAll")
    public List<UserResponse> findAll() {
        return userService.readAll();
    }

    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
        return userService.update(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}