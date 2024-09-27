package jsfas.db.main.persistence.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import jsfas.common.constants.AppConstants;

public class RedisEventHandler implements RedisService {
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(RedisEventHandler.class);
	
	@Override
	public void save(String key, Object object) {
		save(key, object, AppConstants.SESSION_TIMEOUT); 
	}
	
	@Override
	public void save(String key, Object object, int ttl) {
		redisTemplate.opsForValue().set(AppConstants.REDIS_PREFIX + key, object, ttl, TimeUnit.SECONDS);
	}
	
	@Override
	public void delete(String key){
		redisTemplate.delete(AppConstants.REDIS_PREFIX + key);
	}
	
	@Override
	public Object findById(String key) {
		return redisTemplate.opsForValue().get(AppConstants.REDIS_PREFIX + key);
	}
	
}