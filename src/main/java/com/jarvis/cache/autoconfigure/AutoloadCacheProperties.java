package com.jarvis.cache.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.jarvis.cache.to.AutoLoadConfig;

/**
 * @author jiayu.qiu
 */
@ConfigurationProperties(prefix="autoload.cache")
public class AutoloadCacheProperties {

    private AutoLoadConfig config=new AutoLoadConfig();

    private JedisCacheManagerConfig jedis=new JedisCacheManagerConfig();

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
