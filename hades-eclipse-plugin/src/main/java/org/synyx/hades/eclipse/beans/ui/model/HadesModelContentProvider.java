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

package org.synyx.hades.eclipse.beans.ui.model;

import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.springframework.ide.eclipse.beans.core.BeansCorePlugin;
import org.springframework.ide.eclipse.beans.core.internal.model.BeanClassReferences;
import org.springframework.ide.eclipse.beans.core.model.IBean;
import org.springframework.ide.eclipse.beans.core.model.IBeansProject;
import org.springframework.ide.eclipse.beans.ui.model.BeansModelContentProvider;
import org.springframework.ide.eclipse.core.model.IModelElement;
import org.synyx.hades.eclipse.HadesUtils;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class HadesModelContentProvider extends BeansModelContentProvider {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.ide.eclipse.beans.ui.model.BeansModelContentProvider
     * #getJavaTypeChildren(org.eclipse.jdt.core.IType)
     */
    @Override
    protected Object[] getJavaTypeChildren(IType type) {

        IBeansProject project =
                BeansCorePlugin.getModel().getProject(
                        type.getJavaProject().getProject());

        if (project != null) {

            // Add bean references to JDT type
            Set<IBean> beans = project.getBeans(HadesUtils.getFactoryName());
            beans = HadesUtils.getDaoBeans(beans, type.getFullyQualifiedName());

            if (beans != null && beans.size() > 0) {
                return new Object[] { new BeanClassReferences(type, beans) };
            }
        }

        return IModelElement.NO_CHILDREN;
    }
}
