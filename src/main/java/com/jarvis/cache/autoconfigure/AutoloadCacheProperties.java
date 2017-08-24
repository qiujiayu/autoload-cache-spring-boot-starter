package com.jarvis.cache.autoconfigure;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

import com.jarvis.cache.to.AutoLoadConfig;

/**
 * @author jiayu.qiu
 */
@ConfigurationProperties(prefix="autoload.cache")
public class AutoloadCacheProperties {

    private AutoLoadConfig config=new AutoLoadConfig();

    private JedisCacheManagerConfig jedis=new JedisCacheManagerConfig();

    @Autowired
    private Environment env;

    private boolean namespaceEnable=true;
    
    private boolean proxyTargetClass=false;
    
    private boolean enable=true;

    @PostConstruct
    public void init() {
        if(namespaceEnable && null != env) {
            String namespace=config.getNamespace();

            if(null == namespace || namespace.trim().length() == 0) {
                String applicationName=env.getProperty("spring.application.name");
                if(null != applicationName && applicationName.trim().length() > 0) {
                    config.setNamespace(applicationName);
                }
            }
        }

    }

    public AutoLoadConfig getConfig() {
        return config;
    }

    public void setConfig(AutoLoadConfig config) {
        this.config=config;
    }

    public JedisCacheManagerConfig getJedis() {
        return jedis;
    }

    public void setJedis(JedisCacheManagerConfig jedis) {
        this.jedis=jedis;
    }

    public boolean isNamespaceEnable() {
        return namespaceEnable;
    }

    public void setNamespaceEnable(boolean namespaceEnable) {
        this.namespaceEnable=namespaceEnable;
    }

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * 对JedisClusterCacheManager 进行配置
     * @author jiayu.qiu
     */
    static class JedisCacheManagerConfig {

        /**
         * Hash的缓存时长：等于0时永久缓存；大于0时，主要是为了防止一些已经不用的缓存占用内存;hashExpire小于0时，则使用@Cache中设置的expire值（默认值为-1）。
         */
        private int hashExpire=-1;

        /**
         * 是否通过脚本来设置 Hash的缓存时长
         */
        private boolean hashExpireByScript=true;

        public int getHashExpire() {
            return hashExpire;
        }

        public void setHashExpire(int hashExpire) {
            this.hashExpire=hashExpire;
        }

        public boolean isHashExpireByScript() {
            return hashExpireByScript;
        }

        public void setHashExpireByScript(boolean hashExpireByScript) {
            this.hashExpireByScript=hashExpireByScript;
        }

    }
}
