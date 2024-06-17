package com.nhnacademy.gateway2.Controller;

import com.nhnacademy.gateway2.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/api/users/{id}")
    public void getUserById(@PathVariable Long id) {
        userService.getUserById(id);
    }
}