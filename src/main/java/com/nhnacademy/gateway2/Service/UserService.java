package com.nhnacademy.gateway2.Service;

import com.nhnacademy.gateway2.ServiceClient.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 예시
@Service
public class UserService {

    @Autowired
    private UserServiceClient userServiceClient;

    public void getUserById(Long id) {
        userServiceClient.getUserById(id);
    }
}