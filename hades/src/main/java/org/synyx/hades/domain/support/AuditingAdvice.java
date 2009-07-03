/*
 * Copyright 2002-2008 the original author or authors.
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

package org.synyx.hades.domain.support;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.joda.time.DateTime;
import org.synyx.hades.domain.Auditable;
import org.synyx.hades.domain.AuditorAware;
import org.synyx.hades.domain.Persistable;


/**
 * Aspect touching entities being saved. Sets modification date and auditor. If
 * no {@code AuditorAware} is set only modification and creation date will be
 * set.
 * <p>
 * The advice intercepts calls to save methods of
 * {@link org.synyx.hades.dao.GenericDao}. It is implemented using
 * {@link Aspect} annotations to be suitable for all kinds of aspect weaving. To
 * enable capturing audit data simply register the advice and activate
 * annotation based aspect:
 * 
 * <pre>
 * &lt;aop:aspectj-autoproxy /&gt;
 * &lt;bean class=&quot;org.synyx.hades.domain.support.AuditionAdvice&quot; /&gt;
 * </pre>
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <T> the type of the auditing instance
 * @param <PK> the type of the auditing instance's identifier
 */
@Aspect
public class AuditingAdvice<T extends Persistable<PK>, PK extends Serializable> {

    private static final Log LOG = LogFactory.getLog(AuditingAdvice.class);

    private AuditorAware<T> auditorAware;

    private boolean dateTimeForNow = true;
    private boolean modifyOnCreation = true;


    /**
     * Setter to inject a {@code AuditorAware} component to retrieve the current
     * auditor.
     * 
     * @param auditorAware the auditorAware to set
     */
    public void setAuditorAware(final AuditorAware<T> auditorAware) {

        this.auditorAware = auditorAware;
    }


    /**
     * Setter do determine if {@link Auditable#setCreated(DateTime)} and
     * {@link Auditable#setLastModified(DateTime)} shall be filled with the
     * current Java time. Defaults to {@code true}. One might set this to
     * {@code false} to use database features to set entity time.
     * 
     * @param dateTimeForNow the dateTimeForNow to set
     */
    public void setDateTimeForNow(boolean dateTimeForNow) {

        this.dateTimeForNow = dateTimeForNow;
    }


    /**
     * Set this to false if you want to treat entity creation as modification
     * and thus set the current date as modification date, too. Defaults to
     * {@code true}.
     * 
     * @param modifyOnCreation if modification information shall be set on
     *            creation, too
     */
    public void setModifyOnCreation(final boolean modifyOnCreation) {

        this.modifyOnCreation = modifyOnCreation;
    }


    /**
     * Sets modification and creation date and auditor on an auditable entity.
     * 
     * @param auditable
     */
    @Before("execution(* org.synyx.hades.dao.GenericDao+.save*(..)) && args(auditable)")
    public void touch(final Auditable<Persistable<PK>, PK> auditable) {

        if (null == auditable) {
            return;
        }

        T auditor = touchAuditor(auditable);
        DateTime now = dateTimeForNow ? touchDate(auditable) : null;

        // Log touching
        if (LOG.isDebugEnabled()) {

            StringBuilder builder = new StringBuilder("Touched ");
            builder.append(auditable);

            if (null != now) {
                builder.append("Last modification: ").append(now);
            }

            if (null != auditor) {
                builder.append(" by ");
                builder.append(auditor.toString());
            }

            LOG.debug(builder.toString());
        }
    }


    /**
     * Sets modification and creation date and auditor on a {@link List} of
     * {@link Auditable}s.
     * 
     * @param auditables
     */
    @Before("execution(* org.synyx.hades.dao.GenericDao+.save*(..)) && args(auditables)")
    public void touch(final List<Auditable<Persistable<PK>, PK>> auditables) {

        for (Auditable<Persistable<PK>, PK> auditable : auditables) {

            touch(auditable);
        }
    }


    /**
     * Sets modifying and creating auditioner. Creating auditioner is only set
     * on new auditables.
     * 
     * @param auditable
     * @return
     */
    private T touchAuditor(final Auditable<Persistable<PK>, PK> auditable) {

        if (null == auditorAware) {
            return null;
        }

        T auditor = auditorAware.getCurrentAuditor();

        if (auditable.isNew()) {

            auditable.setCreatedBy(auditor);

            if (!modifyOnCreation) {
                return auditor;
            }
        }

        auditable.setLastModifiedBy(auditor);

        return auditor;
    }


    /**
     * Touches the auditable regarding modification and creation date. Creation
     * date is only set on new auditables.
     * 
     * @param auditable
     * @return
     */
    private DateTime touchDate(final Auditable<Persistable<PK>, PK> auditable) {

        DateTime now = new DateTime();

        if (auditable.isNew()) {
            auditable.setCreated(now);

            if (!modifyOnCreation) {
                return now;
            }
        }

        auditable.setLastModified(now);

        return now;
    }
}
