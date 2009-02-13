package org.synyx.hades.eclipse;

import org.springframework.ide.eclipse.beans.ui.editor.contentassist.BeanReferenceContentAssistCalculator;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.IContentAssistContext;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.IContentAssistProposalRecorder;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class HadesContentAssistCalculator extends
        BeanReferenceContentAssistCalculator {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.ide.eclipse.beans.ui.editor.contentassist.
     * BeanReferenceContentAssistCalculator
     * #computeProposals(org.springframework.
     * ide.eclipse.beans.ui.editor.contentassist.IContentAssistContext,
     * org.springframework
     * .ide.eclipse.beans.ui.editor.contentassist.IContentAssistProposalRecorder
     * )
     */
    @Override
    public void computeProposals(IContentAssistContext context,
            IContentAssistProposalRecorder recorder) {

        // TODO Auto-generated method stub
        super.computeProposals(context, recorder);
    }
}
