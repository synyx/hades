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

package org.synyx.hades.eclipse.beans.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.ide.eclipse.beans.core.model.IBean;
import org.springframework.ide.eclipse.beans.core.model.IBeanProperty;
import org.springframework.ide.eclipse.beans.ui.namespaces.DefaultNamespaceContentProvider;


/**
 * Removes DAO factory properties from the outline view.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class HadesNamespaceContentProvider extends
        DefaultNamespaceContentProvider {

    private static final List<String> FILTER_PROPERTIES =
            Arrays.asList("daoInterface", "domainClass");


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.ide.eclipse.beans.ui.model.BeansModelContentProvider
     * #getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object parentElement) {

        // Filter bean properties
        if (parentElement instanceof IBean) {

            List<IBeanProperty> filtered = new ArrayList<IBeanProperty>();

            for (Object child : super.getChildren(parentElement)) {

                if (child instanceof IBeanProperty) {

                    IBeanProperty property = (IBeanProperty) child;
                    if (!FILTER_PROPERTIES.contains(property.getElementName())) {
                        filtered.add(property);
                    }
                }
            }

            return filtered.toArray();
        }

        return super.getChildren(parentElement);
    }
}
