/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hilling.cdi.sampleapp.ejb;

import de.hilling.cdi.sampleapp.UserRegistrationEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RegistrationService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method takes a name and returns a personalised greeting.
     *
     * @param name the name of the person to be greeted
     * @return the personalised greeting.
     */
    public String register(String name) {
        UserRegistrationEntity userRegistrationEntity = new UserRegistrationEntity();
        String greeting = "Hello " + name;
        userRegistrationEntity.setUserName(greeting);
        entityManager.persist(userRegistrationEntity);
        return greeting;
    }
}