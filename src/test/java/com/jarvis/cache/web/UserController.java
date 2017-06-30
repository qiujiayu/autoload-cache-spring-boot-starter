package com.jarvis.cache.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jarvis.cache.service.UserService;
import com.jarvis.cache.to.User;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/user/{id}")
    public User list(@PathVariable Integer id) {
        System.out.println("getuser" + id);
        return userService.getUserById(id);
    }

    @RequestMapping("/update1/{id}")
    public void update1(@PathVariable Integer id) {
        User user=new User();
        user.setId(id);
        userService.updateUser(user);
    }

    @RequestMapping("/update2/{id}")
    public void update2(@PathVariable Integer id) {
        User user=new User();
        user.setId(id);
        userService.updateUser2(user);
    }
}
