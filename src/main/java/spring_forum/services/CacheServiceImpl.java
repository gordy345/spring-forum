package spring_forum.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    private static final int TIME_TO_LIVE = 60;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public CacheServiceImpl(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String get(String key) {
        log.info("Getting value from cache for key: " + key);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public byte[] getImage(String key) {
        log.info("Getting image from cache for key: " + key);
        return Base64.getDecoder().decode(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void put(String key, String value) {
        log.info("Setting new cache value for key: " + key);
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, TIME_TO_LIVE, TimeUnit.MINUTES);
    }

    @Override
    public void putImage(String key, byte[] image) {
        log.info("Setting new image cache value for key: " + key);
        redisTemplate.opsForValue().set(key, Base64.getEncoder().encodeToString(image));
        redisTemplate.expire(key, TIME_TO_LIVE, TimeUnit.MINUTES);
    }

    @Override
    public boolean containsKey(String key) {
        log.info("Checking for cache key existence: " + key);
        return redisTemplate.opsForValue().get(key) != null;
    }

    @Override
    public void remove(String... keys) {
        log.info("Removing keys from cache: " + Arrays.toString(keys));
        if (keys.length == 0) {
            return;
        }
        for (String key : keys) {
            redisTemplate.opsForValue().getAndDelete(key);
        }
    }

    @Override
    public void remove(List<String> keys) {
        log.info("Removing keys from cache: " + keys);
        if (keys.size() == 0) {
            return;
        }
        keys.forEach(redisTemplate.opsForValue()::getAndDelete);
    }

}
