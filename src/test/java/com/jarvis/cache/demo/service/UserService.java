package com.jarvis.cache.demo.service;

import java.util.List;

import com.jarvis.cache.demo.condition.UserCondition;
import com.jarvis.cache.demo.entity.UserDO;

public interface UserService {

    UserDO getUserById(Long userId);

    List<UserDO> listByCondition(UserCondition condition);
    
    Long register(UserDO user);
    
    UserDO doLogin(String name, String password);

    void updateUser(UserDO user);

    void deleteUserById(Long userId);
}
