
package com.jarvis.cache.demo.test;

import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.jarvis.cache.demo.condition.UserCondition;
import com.jarvis.cache.demo.entity.UserDO;
import com.jarvis.cache.demo.service.UserService;


/**
 * @author: jiayu.qiu
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest extends BaseServiceTest {
    @Autowired
    private UserService userService;

    @Test
    @Transactional
    @Rollback(true)
    public void test1Add() throws Exception {
        UserDO userDO = UserDO.builder().name("tmp").password("aaaa").build();
        Long userId = userService.register(userDO);
        
        userDO = UserDO.builder().name("tmp2").password("aaaa2").build();
        userId = userService.register(userDO);
        
        userDO = UserDO.builder().id(userId).name("tmp2").password("aaaa3").build();
        userService.updateUser(userDO);

        userService.doLogin("tmp2", "aaaa3");
        
        
        UserCondition condition = new UserCondition();
        Pageable pageable = new PageRequest(1, 10);
        condition.setPageable(pageable);

        List<UserDO> list = userService.listByCondition(condition);
        Assert.assertNotNull(list);
        for (UserDO user : list) {
            System.out.println("list item --->" + user);
        }

        userDO = userService.getUserById(userId);
        System.out.println("detail-->" + userDO);
        userDO = userService.getUserById(userId);
        System.out.println("detail-->" + userDO);
    }

}
