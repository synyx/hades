package org.synyx.hades.eclipse.beans.ui.editor;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.springframework.ide.eclipse.beans.ui.editor.namespaces.DefaultReferenceableElementsLocator;
import org.synyx.hades.eclipse.HadesUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class HadesReferenceableElementLocator extends
        DefaultReferenceableElementsLocator {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.ide.eclipse.beans.ui.editor.namespaces.
     * IReferenceableElementsLocator
     * #getReferenceableElements(org.w3c.dom.Document,
     * org.eclipse.core.resources.IFile)
     */
    public Map<String, Node> getReferenceableElements(Document document,
            IFile file) {

        Map<String, Node> result =
                super.getReferenceableElements(document, file);

        NodeList childNodes = document.getDocumentElement().getChildNodes();
        Node hadesElement = null;

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (HadesUtils.isHadesElement(node)) {
                hadesElement = node;
                break;
            }
        }

        for (String name : HadesUtils.getDaoBeanIds()) {

            result.put(name, hadesElement);
        }

        return result;
    }
}
