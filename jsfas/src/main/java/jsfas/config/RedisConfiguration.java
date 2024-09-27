package jsfas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import jsfas.common.PasswordProtector;

@Configuration
@EnableRedisRepositories(
		basePackages = {"jsfas.redis"},
		enableKeyspaceEvents = EnableKeyspaceEvents.ON_STARTUP) 
@EnableCaching
public class RedisConfiguration {

	@Autowired
	private Environment env;

    @Autowired
	PasswordProtector pp;
    
    private static final Logger log = LoggerFactory.getLogger(RedisConfiguration.class);

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
    	RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    	redisStandaloneConfiguration.setHostName(env.getProperty("redis.host", "localhost"));
    	redisStandaloneConfiguration.setPort(env.getProperty("redis.port", Integer.class, 6379));
    	redisStandaloneConfiguration.setDatabase(env.getProperty("redis.database",Integer.class, 1));
    	//redisStandaloneConfiguration.setPassword(pp.decrypt(env.getProperty("redis.redispassword", "redispassword")));
		
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(RedisSerializer.json());
        
        return template;
    }
}
