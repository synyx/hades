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
package org.synyx.hades.dao.orm;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.persistence.platform.database.HSQLPlatform;
import org.springframework.orm.jpa.JpaVendorAdapter;


/**
 * Workaround for a bug in EclipseLink when using HSQL as database. Be sure you
 * configure this platform on your {@link JpaVendorAdapter} if you are using
 * Eclipselink with HSQL.
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=240618
 * @author Oliver Gierke - gierke@synyx.de
 */
public class EclipseLinkHSQLPlatform extends HSQLPlatform {

    private static final long serialVersionUID = -6272530728100183786L;


    /**
     * Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=240618.
     * 
     * @return false
     */
    @Override
    public boolean supportsUniqueKeyConstraints() {

        return false;
    }


    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.persistence.internal.databaseaccess.DatabasePlatform#
     * printFieldUnique(java.io.Writer, boolean)
     */
    @Override
    public void printFieldUnique(Writer writer,
            boolean shouldPrintFieldIdentityClause) throws IOException {

    }
}
