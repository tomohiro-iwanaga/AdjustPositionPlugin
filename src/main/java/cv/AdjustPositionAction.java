package cv;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;

public class AdjustPositionAction implements IPluginActionDelegate {

    public Object run(IWindow window) throws UnExpectedException {
        try {

            TransactionManager.beginTransaction();
            IPresentation[] selectedPresentations = getSelectedPresentaions();
            List<ILinkPresentation> linkPresentaions = pickLinkPresentations(selectedPresentations);
            PositionAdjuster adjuster = new PositionAdjuster();
            adjuster.adjust(linkPresentaions);
            TransactionManager.endTransaction();

        } catch (ProjectNotFoundException e) {
            String message = "Project is not opened.Please open the project or create new project.";
            JOptionPane.showMessageDialog(window.getParent(), message, "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.",
                    "Alert", JOptionPane.ERROR_MESSAGE);
            throw new UnExpectedException();
        }
        return null;
    }
    
    private IPresentation[] getSelectedPresentaions()
            throws ClassNotFoundException, ProjectNotFoundException, InvalidUsingException {
        AstahAPI api = AstahAPI.getAstahAPI();
        ProjectAccessor projectAccessor = api.getProjectAccessor();
        IDiagramViewManager viewManager = projectAccessor.getViewManager().getDiagramViewManager();
        return viewManager.getSelectedPresentations();
    }
    
    private List<ILinkPresentation> pickLinkPresentations(IPresentation[] presentations) {
        List<ILinkPresentation> result = new ArrayList<>();
        for (IPresentation presentation : presentations) {
            if (presentation instanceof ILinkPresentation) {
                result.add((ILinkPresentation) presentation);
            }
        }
        return result;
    }
}
