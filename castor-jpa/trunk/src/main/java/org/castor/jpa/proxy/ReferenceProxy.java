/*
 * Copyright 2010 Lukas Lang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.castor.jpa.proxy;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;

import org.exolab.castor.persist.spi.Identity;

/**
 * A CGLib implementation of the {@link EntityProxy} interface.
 */
public class ReferenceProxy<T> implements EntityProxy {

    private Class<T> type;
    private Identity identity;
    private T entity;
    private EntityManager entityManager;

    private ReferenceProxy(Class<T> type, Identity identity, EntityManager entityManager) {
        this.type = type;
        this.identity = identity;
        this.entityManager = entityManager;
    }

    /**
     * Returns a new proxy instance for the given parameters.
     * 
     * @param <T> the entity's type.
     * @param clazz the entity's class.
     * @param identity the entity's identity.
     * @param entityManager the {@link EntityManager} to use.
     * @return a new proxy instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> clazz, Identity identity, EntityManager entityManager) {
        ReferenceProxy<T> proxy = new ReferenceProxy<T>(clazz, identity, entityManager);
        return (T) Enhancer.create(clazz, new Class[] { EntityProxy.class }, proxy);
    }

    public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (isSame("getIdentity", method)) {
            return getIdentity();
        } else if (isSame("isLoaded", method)) {
            return isLoaded();
        } else if (isSame("finalze", method)) {
            return method.invoke(object, args);
        } else if (isSame("equals", method)) {
            return method.invoke(object, args);
        }

        // Check whether entity has been loaded.
        if (!isLoaded()) {
            find();
        }
        return method.invoke(this.entity, args);
    }

    /**
     * @return true if name and name of method are equals, false otherwise.
     */
    private boolean isSame(String name, Method method) {
        return name.equals(method.getName());
    }

    /**
     * Loads the entity from the {@link EntityManager}.
     */
    private synchronized void find() {
        this.entity = this.entityManager.find(this.type, this.identity);
        if (this.entity == null) {
            throw new EntityNotFoundException("Entity of type >" + this.type.getName() + "< with identity >"
                    + this.identity.toString() + "< does not exist!");
        }
    }

    public Identity getIdentity() {
        return this.identity;
    }

    public boolean isLoaded() {
        return this.entity == null ? false : true;
    }
}