package com.jarvis.cache.service;

import com.jarvis.cache.to.User;

public interface UserService {

    User getUserById(Integer id);

    void updateUser(User user);

    void updateUser2(User user);
}
