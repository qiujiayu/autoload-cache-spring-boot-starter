package com.jarvis.cache.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jarvis.cache.annotation.CacheDeleteTransactional;
import com.jarvis.cache.demo.dao.UserDAO;
import com.jarvis.cache.demo.mapper.ClientUpgradeMapper;
import com.jarvis.cache.demo.to.User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ClientUpgradeMapper clientUpgradeMapper;

    @Override
    public User getUserById(Integer id) {
        // 测试Mybatis
        System.out.println(clientUpgradeMapper.getById(83));
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
