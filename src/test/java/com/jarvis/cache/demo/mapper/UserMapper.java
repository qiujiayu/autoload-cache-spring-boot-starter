package com.jarvis.cache.demo.mapper;

import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.annotation.CacheDelete;
import com.jarvis.cache.annotation.CacheDeleteKey;
import com.jarvis.cache.demo.entity.UserDO;

/**
 * 在接口中使用注解的例子
 * @author jiayu.qiu
 *
 */
public interface UserMapper {

    /**
     * @param id
     * @return
     */
    @Cache(expire=300, expireExpression="null==#retVal ? 60: 300", key="'user:getById:'+#args[0]", condition="#args[0]>0")
    UserDO getById(Integer id);

    /**
     * @param user
     */
    int addUser(UserDO user);

    /**
     * @param user
     */
    @CacheDelete({@CacheDeleteKey(value="'user:getById:'+#args[0].id", condition="#retVal > 0")})
    int updateUser(UserDO user);

}