package com.jarvis.cache.dao;

import org.springframework.stereotype.Component;

import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.annotation.CacheDelete;
import com.jarvis.cache.annotation.CacheDeleteKey;
import com.jarvis.cache.to.User;

@Component
public class UserDAO {

    @Cache(expire=120, key="'user'+#args[0]")
    public User getUserById(Integer id) {
        User user=new User();
        user.setId(id);
        user.setAge(id + 10);
        user.setName("name" + id);
        System.out.println("load data form db");
        return user;
    }

    @CacheDelete({@CacheDeleteKey({"'user'+#args[0].id"})})
    public void updateUser(User user) {

    }
}
