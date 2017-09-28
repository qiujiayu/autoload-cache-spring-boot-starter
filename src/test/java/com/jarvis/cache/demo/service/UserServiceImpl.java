package com.jarvis.cache.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jarvis.cache.annotation.CacheDeleteTransactional;
import com.jarvis.cache.demo.entity.UserDO;
import com.jarvis.cache.demo.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDO getUserById(Integer id) {
        return userMapper.getById(id);
    }
    
    @Override
    public void add(UserDO user) {
        userMapper.addUser(user);
    }

    @Override
    @CacheDeleteTransactional
    public void updateUser(UserDO user) {
        userMapper.updateUser(user);
    }

}
