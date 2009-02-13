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
