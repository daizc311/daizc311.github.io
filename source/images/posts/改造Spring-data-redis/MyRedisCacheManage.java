package com.kailinjt.cloud.common.redis.support;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @ClassName MyRedisCacheManage
 * @Description 自定义的redisCache，用于@Cacheable定义过期时间
 * @Author tianhao
 * @Date 2020/4/28 17:35
 * @Version 1.0
 */
public class MyRedisCacheManage extends RedisCacheManager {


    //校验规则：获取时间
    private static final String REGEX_STR = "\\w+##\\d+$";

    private final RedisCacheWriter cacheWriter;
    private final RedisCacheConfiguration defaultCacheConfig;

    public MyRedisCacheManage(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    public MyRedisCacheManage(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, String... initialCacheNames) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheNames);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    public MyRedisCacheManage(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, boolean allowInFlightCacheCreation, String... initialCacheNames) {
        super(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation, initialCacheNames);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    public MyRedisCacheManage(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    public MyRedisCacheManage(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    /**
     * 用于返回自定义的redisCache
     **/
    @Override
    @NonNull
    protected RedisCache createRedisCache(@NonNull String originalName, RedisCacheConfiguration cacheConfig) {

        // 符合正则格式的使用 TTLRedisCache
        if (Pattern.matches(REGEX_STR, originalName)) {
            List<String> keyList = Lists.newArrayList(Splitter.on("##").split(originalName));
            // 从originalName中截取出的name
            String finalName = keyList.get(0);
            // 从originalName中截取出的TTL
            Long TTL = Long.valueOf(keyList.get(1));
            return new TTLRedisCache(finalName, cacheWriter, cacheConfig != null ? cacheConfig : defaultCacheConfig, TTL);
        }

        return super.createRedisCache(originalName, cacheConfig);
    }
}
