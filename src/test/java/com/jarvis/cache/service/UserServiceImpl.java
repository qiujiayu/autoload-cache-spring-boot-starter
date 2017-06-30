package com.jarvis.cache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jarvis.cache.annotation.CacheDeleteTransactional;
import com.jarvis.cache.dao.UserDAO;
import com.jarvis.cache.to.User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public User getUserById(Integer id) {
        return userDAO.getUserById(id);
    }

    @Override
    public void updateUser(User user) {
        userDAO.updateUser(user);
    }

    @CacheDeleteTransactional
    @Override
    public void updateUser2(User user) {
        userDAO.updateUser(user);
    }

}
