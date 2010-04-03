/*
 * Copyright 2008-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.synyx.hades.dao.orm;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.openjpa.persistence.OpenJPAQuery;
import org.eclipse.persistence.jpa.JpaQuery;
import org.hibernate.ejb.HibernateQuery;
import org.synyx.hades.dao.query.QueryExtractor;
import org.synyx.hades.util.ClassUtils;


/**
 * Enumeration representing peristence providers to be used with Hades.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
enum PersistenceProvider implements QueryExtractor {

    /**
     * Hibernate persistence provider.
     */
    HIBERNATE(GenericHibernateJpaDao.class,
            "org.hibernate.ejb.HibernateEntityManager") {

        public String extractQueryString(Query query) {

            return ((HibernateQuery) query).getHibernateQuery()
                    .getQueryString();
        }


        public boolean canExtractQuery() {

            return true;
        }
    },

    /**
     * EclipseLink persistence provider.
     */
    ECLIPSELINK(GenericEclipseLinkJpaDao.class,
            "org.eclipse.persistence.jpa.JpaEntityManager") {

        public String extractQueryString(Query query) {

            return ((JpaQuery) query).getDatabaseQuery().getJPQLString();
        }


        public boolean canExtractQuery() {

            return true;
        }
    },

    /**
     * OpenJpa persistence provider.
     */
    OPEN_JPA(GenericJpaDao.class,
            "org.apache.openjpa.persistence.OpenJPAEntityManager") {

        public String extractQueryString(Query query) {

            return ((OpenJPAQuery) query).getQueryString();
        }


        public boolean canExtractQuery() {

            return true;
        }
    },

    /**
     * Unknown special provider. Use standard JPA.
     */
    GENERIC_JPA(GenericJpaDao.class, "javax.persistence.EntityManager") {

        public String extractQueryString(Query query) {

            return null;
        }


        public boolean canExtractQuery() {

            return false;
        }
    };

    @SuppressWarnings("unchecked")
    private Class<? extends GenericJpaDao> daoBaseClass;
    private String entityManagerClassName;


    /**
     * Creates a new {@link PersistenceProvider}.
     * 
     * @param daoBaseClass the DAO base class to be used with the persistence
     *            provider
     * @param entityManagerClassName the name of the provider specific
     *            {@link EntityManager} implementation
     */
    @SuppressWarnings("unchecked")
    private PersistenceProvider(Class<? extends GenericJpaDao> daoBaseClass,
            String entityManagerClassName) {

        this.daoBaseClass = daoBaseClass;
        this.entityManagerClassName = entityManagerClassName;
    }


    /**
     * Returns the DAO base class to be used.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public Class<? extends GenericJpaDao> getDaoBaseClass() {

        return daoBaseClass;
    }


    /**
     * Determines the {@link PersistenceProvider} from the given
     * {@link EntityManager}. If no special one can be determined
     * {@value #GENERIC_JPA} will be returned.
     * 
     * @param em
     * @return
     */
    public static PersistenceProvider fromEntityManager(EntityManager em) {

        for (PersistenceProvider provider : values()) {
            if (ClassUtils.isEntityManagerOfType(em,
                    provider.entityManagerClassName)) {
                return provider;
            }
        }

        return GENERIC_JPA;
    }
}
