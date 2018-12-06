package com.jarvis.cache.demo.service;

import java.util.List;

import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.annotation.CacheDeleteTransactional;
import com.jarvis.cache.demo.condition.UserCondition;
import com.jarvis.cache.demo.entity.UserDO;

public interface UserService {

    UserDO getUserById(Long userId);

    // @Cache(expire = 600, key = "'userid-list-' + #hash(#args[0])")
    List<UserDO> listByCondition(UserCondition condition);

    // @CacheDeleteTransactional
    Long register(UserDO user);

    Long getUserIdByName(String name);

    UserDO doLogin(String name, String password);

    void updateUser(UserDO user);

    void deleteUserById(Long userId);
}
