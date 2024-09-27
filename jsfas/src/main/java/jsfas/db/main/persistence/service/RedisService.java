package jsfas.db.main.persistence.service;

/**
 * Redis Service
 * @author iswill
 * @since 3/6/2021
 */
public interface RedisService {
	public void save(String key, Object object);
	public void save(String key, Object object, int ttl);
	public void delete(String key);
	public Object findById(String key);
}
