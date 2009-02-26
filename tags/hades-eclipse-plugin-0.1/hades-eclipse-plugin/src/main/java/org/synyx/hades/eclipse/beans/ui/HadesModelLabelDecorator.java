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

import java.util.Set;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IDecoration;
import org.springframework.ide.eclipse.beans.ui.BeansUIImages;
import org.springframework.ide.eclipse.beans.ui.model.BeansModelLabelDecorator;
import org.synyx.hades.eclipse.HadesUtils;


/**
 * Decorates the actual DAO interfaces with the Spring bean marker.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class HadesModelLabelDecorator extends BeansModelLabelDecorator {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.ide.eclipse.beans.ui.model.BeansModelLabelDecorator
     * #decorateJavaElement(org.eclipse.jdt.core.IJavaElement,
     * org.eclipse.jface.viewers.IDecoration)
     */
    @Override
    protected void decorateJavaElement(IJavaElement element,
            IDecoration decoration) {

        int type = element.getElementType();

        try {

            Set<String> daoInterfaces = HadesUtils.getDaoInterfaceNames();

            if (type == IJavaElement.CLASS_FILE) {

                // Decorate Java class file
                IType javaType = ((IClassFile) element).getType();

                if (daoInterfaces.contains(javaType.getFullyQualifiedName())) {
                    decoration.addOverlay(BeansUIImages.DESC_OVR_SPRING);
                }

            } else if (type == IJavaElement.COMPILATION_UNIT) {

                // Decorate Java source file
                for (IType javaType : ((ICompilationUnit) element).getTypes()) {
                    if (daoInterfaces
                            .contains(javaType.getFullyQualifiedName())) {
                        decoration.addOverlay(BeansUIImages.DESC_OVR_SPRING);
                        break;
                    }
                }
            }

        } catch (JavaModelException e) {
            // ignore
        }

        super.decorateJavaElement(element, decoration);
    }
}
