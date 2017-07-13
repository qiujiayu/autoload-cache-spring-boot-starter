package com.jarvis.cache.demo.service;

import com.jarvis.cache.demo.to.User;

public interface UserService {

    User getUserById(Integer id);

    void updateUser(User user);

    void updateUser2(User user);
}
