package com.jarvis.cache.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.jarvis.cache.to.AutoLoadConfig;

@ConfigurationProperties(prefix="autoload.cache")
public class AutoloadCacheProperties {

    private AutoLoadConfig config=new AutoLoadConfig();

    public AutoLoadConfig getConfig() {
        return config;
    }

    public void setConfig(AutoLoadConfig config) {
        this.config=config;
    }

}
