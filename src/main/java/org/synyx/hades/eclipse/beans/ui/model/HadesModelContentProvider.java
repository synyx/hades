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
