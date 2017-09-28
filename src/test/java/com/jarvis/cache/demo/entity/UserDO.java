package com.jarvis.cache.demo.entity;

import java.io.Serializable;

public class UserDO implements Serializable {

    private static final long serialVersionUID=1932703849895844645L;

    private Integer id;

    private String name;
    
    private String password;

    private Integer age;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age=age;
    }

}
