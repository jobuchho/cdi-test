package de.hilling.junit.cdi.scope.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.spi.Bean;

public class CustomScopeContextHolder implements ScopeContextHolder {
    private Map<Class<?>, CustomScopeInstance<?>> beans;

    public CustomScopeContextHolder() {
        beans = Collections.synchronizedMap(new HashMap<Class<?>, CustomScopeInstance<?>>());
    }

    @Override
    public Map<Class<?>, CustomScopeInstance<?>> getBeans() {
        return beans;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CustomScopeInstance<T> getBean(Class<T> type) {
        return (CustomScopeInstance<T>) beans.get(type);
    }

    @Override
    public void putBean(CustomScopeInstance<?> customInstance) {
        beans.put(customInstance.bean.getBeanClass(), customInstance);
    }

    @Override
    public void clear() {
        for (CustomScopeInstance<?> scopeInstance : beans.values()) {
            destroy(scopeInstance);
        }
        beans.clear();
    }

    private <T> void destroy(CustomScopeInstance<T> scopeInstance) {
        Bean<T> bean = scopeInstance.bean;
        bean.destroy(scopeInstance.instance, scopeInstance.ctx);
    }

}
