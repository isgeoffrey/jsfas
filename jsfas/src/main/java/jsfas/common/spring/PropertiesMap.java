package jsfas.common.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.StringValueResolver;

/**
 * This class extends the spring standard PropertyPlaceholderConfigurer to support indefinite level of placeholder in properties file
 * 
 * @author isalister
 * @since 26/2/2018
 * @version 1.0
 */
public class PropertiesMap extends PropertyPlaceholderConfigurer implements Map<String, String>{

    Map<String, String> propsMap = new HashMap<String, String>();
    
    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
        this.propsMap.clear();
        
        for(Entry<Object, Object> entry: props.entrySet()) {
            this.propsMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        
        super.processProperties(beanFactory, props);
    }
    
    @Override
    protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess, StringValueResolver valueResolver) {
        super.doProcessProperties(beanFactoryToProcess, valueResolver);
        
        for(Entry<String, String> entry: propsMap.entrySet()) {
            entry.setValue(valueResolver.resolveStringValue(entry.getValue()));
        }
    }
    
    
    @Override
    public int size() {
        // TODO Auto-generated method stub
        return propsMap.size();
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return propsMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        // TODO Auto-generated method stub
        return propsMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        // TODO Auto-generated method stub
        return propsMap.containsValue(value);
    }

    @Override
    public String get(Object key) {
        // TODO Auto-generated method stub
        return propsMap.get(key);
    }

    @Override
    public String put(String key, String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public String remove(Object key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        // TODO Auto-generated method stub
        return propsMap.keySet();
    }

    @Override
    public Collection<String> values() {
        // TODO Auto-generated method stub
        return propsMap.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        // TODO Auto-generated method stub
        return propsMap.entrySet();
    }

}
