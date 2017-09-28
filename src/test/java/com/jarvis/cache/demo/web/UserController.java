package com.jarvis.cache.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jarvis.cache.demo.entity.UserDO;
import com.jarvis.cache.demo.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/{id}")
    public UserDO list(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @RequestMapping("/add")
    public UserDO add() {
        UserDO user=new UserDO();
        user.setName("name_"+System.currentTimeMillis());
        user.setPassword("11111");
        user.setAge(20);
        userService.add(user);
        return user;
    }
    
    @RequestMapping("/update/{id}")
    public void update(@PathVariable Integer id) {
        UserDO user=new UserDO();
        user.setId(id);
        user.setName("name:"+id);
        userService.updateUser(user);
    }

}
