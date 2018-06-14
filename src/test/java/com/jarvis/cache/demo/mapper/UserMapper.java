package com.jarvis.cache.demo.mapper;

import java.util.List;

import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.annotation.CacheDelete;
import com.jarvis.cache.annotation.CacheDeleteKey;
import com.jarvis.cache.demo.condition.UserCondition;
import com.jarvis.cache.demo.entity.UserDO;
import com.jarvis.cache.demo.mapper.temp.BaseMapper;

/**
 * 在接口中使用注解的例子 业务背景：用户表中有id, name, password,
 * status字段，name字段是登录名。并且注册成功后，用户名不允许被修改。
 * 
 * @author jiayu.qiu
 */
public interface UserMapper  extends BaseMapper<UserDO, Long>{
    String CACHE_NAME = "user2";
    
    default String getCacheName() {
        return CACHE_NAME;
    }
    /**
     * 根据用户id获取用户信息
     * 
     * @param id
     * @return
     */
    @Cache(expire = 3600, expireExpression = "null == #retVal ? 600: 3600", key = "'user-byid-' + #args[0]")
    UserDO getUserById(Long id);
    
    /**
     * 
     * 测试 autoload = true
     * @return
     */
    @Cache(expire = 3600, key = "user-all", autoload = true)
    List<UserDO> allUsers();
    
    /**
     * 
     * 测试 autoload = true
     * @return
     */
    @Cache(expire = 1200, key = "'user-list-' + @@hash(#args[0])", autoload = true)
    List<UserDO> listByCondition(UserCondition condition);

    /**
     * 根据用户名获取用户id
     * 
     * @param name
     * @return
     */
    @Cache(expire = 1200, expireExpression = "null == #retVal ? 120: 1200", key = "'userid-byname-' + #args[0]")
    Long getUserIdByName(String name);

    /**
     * 根据动态组合查询条件，获取用户id列表
     * 
     * @param condition
     * @return
     **/
    List<Long> listIdsByCondition(UserCondition condition);

    /**
     * 添加用户信息
     * 
     * @param user
     */
    @CacheDelete({ @CacheDeleteKey(value = "'userid-byname-' + #args[0].name") })
    int addUser(UserDO user);

    /**
     * 更新用户信息
     * 
     * @param user
     * @return
     */
    @CacheDelete({ @CacheDeleteKey(value = "'user-byid-' + #args[0].id", condition = "#retVal > 0") })
    int updateUser(UserDO user);

    /**
     * 根据用户id删除用户记录
     **/
    @CacheDelete({ @CacheDeleteKey(value = "'user-byid-' + #args[0]", condition = "#retVal > 0") })
    int deleteUserById(Long id);

}