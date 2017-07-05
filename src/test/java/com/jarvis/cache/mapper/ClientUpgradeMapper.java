package com.jarvis.cache.mapper;

import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.to.ClientUpgrade;

public interface ClientUpgradeMapper {

    /**
     * @param id
     * @return
     */
    @Cache(expire=120, key="'checkUpgrade:getById:'+#args[0]")
    ClientUpgrade getById(Integer id);

}