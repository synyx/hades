package org.synyx.hades.eclipse.beans.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.ide.eclipse.beans.core.model.IBean;
import org.springframework.ide.eclipse.beans.core.model.IBeanProperty;
import org.springframework.ide.eclipse.beans.ui.namespaces.DefaultNamespaceContentProvider;


public class HadesNamespaceContentProvider extends DefaultNamespaceContentProvider {

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
