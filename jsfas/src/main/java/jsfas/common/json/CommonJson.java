package jsfas.common.json;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import jsfas.common.utils.GeneralUtil;

/**
 * This Class use a Map to store key, value(Object) pair. The Map will then convert to JSON Object in JSON parsing process.
 * To make dynamic use. Object instead of String used as type of value in the map.
 * 
 * @author iswill, isalister
 * @since 5/1/2017
 * @version 3.0
 */
public class CommonJson implements Serializable {		

    /**
	 * 
	 */
	private static final long serialVersionUID = 4633190176918458684L;
	
	private Map<String, Object> props = new LinkedHashMap<String, Object>();
    
    /**
     * Set value of the key
     * @param key
     * @param value
     */
    @JsonAnySetter
    public CommonJson set(String key, Object value) {
        this.props.put(key, value);
        return this;
    }
    
    /**
	 * Returns the properties map
	 * @return the map of this CommonJson object
	 */
    @JsonAnyGetter
    public Map<String, Object> getProps() {
        return props;
    }
    
	/**
	 * Alias of getProp(). Returns the properties map
	 * @return the map of this CommonJson object
	 */
	public Map<String, Object> props() {
        return getProps();
    }
    
    /**
     * Get the String value from key
     * @param key
     * @return value of the key
     */
    public String get(String key) {
        return get(key, String.class);
    }
    
    /**
     * Get the filter String value from key.
     * This method should be used when variable passed from web request
     * @param key
     * @return filtered value of the key or empty string to " " if the value is null
     */
    public String getFilterValue(String key) {
        return GeneralUtil.initBlankString(get(key, String.class));
    }
    
    /**
	 * Get the value from key and cast to target type
	 * @param key the property name to resolve
	 * @param targetType the expected type of the property value
	 * @return value of the key or null if fail to cast
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> targetType) {
		if (this.props.containsKey(key)) {
	        Object object = this.props.get(key);
	        if (targetType != null && !targetType.isInstance(object)) {
	        	return null;
	        }
	        return (T) object;
	    }
		return null;
	}
	
	/**
	 * Get the value from key and cast to target type, return defaultValue if this map contains no mapping for the key.
	 * @param key the property name to resolve
	 * @param defaultValue the default mapping of the key
	 * @param targetType the expected type of the property value
	 * @return value of the key or null if fail to cast
	 */
	public <T> T getOrDefault(String key, T defaultValue, Class<T> targetType) {
		T object = this.get(key, targetType);
		return object == null ? defaultValue : object;
	}
	
	/**
	 * Returns the String value to which the specified key is mapped, or defaultValue if this map contains no mapping for the key.
	 * @param key the property name to resolve
	 * @param defaultValue the default mapping of the key
	 */
	public String getOrDefault(String key, String defaultValue) {
		return this.getOrDefault(key, defaultValue, String.class);
	}
	
	/**
	 * Returns the value to which the specified key is mapped, or defaultValue if this map contains no mapping for the key.
	 * @param key the property name to resolve
	 * @param defaultValue the default mapping of the key
	 */
	public Object getOrDefault(String key, Object defaultValue) {
		return this.props().getOrDefault(key, defaultValue);
	}
	
	/**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of key-value mappings in map of this CommonJson object
     */
	public int size() {
		return props.size();
	}
	
	/**
     * Returns <tt>true</tt> if this CommonJson object contains no key-value mappings.
     *
     * @return <tt>true</tt> if this CommonJson object contains no key-value mappings
     */
	@JsonIgnore
	public boolean isEmpty() {
		return props.isEmpty();
	}
	
	@Override
    public boolean equals(Object obj) {
        if(obj instanceof CommonJson) {
            CommonJson commonJson = (CommonJson)obj;
            return this.getProps().equals(commonJson.getProps());
        }
        return false;
    }
	
	@Override
	public int hashCode() {
	    return this.getProps().hashCode();
	}
	
	@Override
	public String toString() {
	    return this.getProps().toString();
	}
	
	public JSONObject toJSONObject() {
		ObjectMapper mapper = new ObjectMapper();
	    JSONObject json;
		try {
			json = new JSONObject(mapper.writeValueAsString(this.props));
		} catch (Exception e) {
			json = new JSONObject();
		}
	    return json;
	}

}
