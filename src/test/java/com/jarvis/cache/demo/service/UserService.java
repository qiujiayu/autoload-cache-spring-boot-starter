package com.jarvis.cache.demo.service;

import com.jarvis.cache.demo.entity.UserDO;

public interface UserService {

    UserDO getUserById(Integer id);

    void updateUser(UserDO user);

    void add(UserDO user);

}
