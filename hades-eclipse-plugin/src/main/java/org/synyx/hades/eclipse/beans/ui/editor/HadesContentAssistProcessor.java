package org.synyx.hades.eclipse.beans.ui.editor;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.BeanReferenceContentAssistCalculator;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.IContentAssistCalculator;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.NamespaceContentAssistProcessorSupport;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.PackageContentAssistCalculator;


/**
 * {@link IContentAssistProcessor} to enable content assists for namespace XML
 * attributes.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class HadesContentAssistProcessor extends
        NamespaceContentAssistProcessorSupport {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.ide.eclipse.beans.ui.editor.contentassist.
     * AbstractContentAssistProcessor#init()
     */
    @Override
    public void init() {

        IContentAssistCalculator calculator =
                new PackageContentAssistCalculator();

        registerContentAssistCalculator("dao-package-name", calculator);
        registerContentAssistCalculator("entity-package-name", calculator);

        registerContentAssistCalculator("dao", "custom-impl-ref",
                new BeanReferenceContentAssistCalculator(true));
    }
}
