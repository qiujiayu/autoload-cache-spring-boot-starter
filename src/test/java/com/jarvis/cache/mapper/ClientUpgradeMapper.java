package com.jarvis.cache.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.to.ClientUpgrade;

@Mapper
public interface ClientUpgradeMapper {

    /**
     * @param id
     * @return
     */
    @Cache(expire=120, key="'checkUpgrade:getById:'+#args[0]")
    ClientUpgrade getById(Integer id);

}