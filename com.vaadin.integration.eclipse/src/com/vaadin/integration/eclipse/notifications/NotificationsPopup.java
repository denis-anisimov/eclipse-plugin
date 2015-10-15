package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;

/**
 * Shows a list of all notifications and allows to navigate to the specific
 * notification.
 * 
 * @author denis
 *
 */
class NotificationsPopup extends AbstractNotificationPopup {

    private Listener mouseListener = new ClickListener();

    private Composite notificationsList;

    private StackLayout mainLayout;

    NotificationsPopup(Display display) {
        super(display);
        setDelayClose(-1);
    }

    @Override
    public void create() {
        super.create();
        mouseListener = new ClickListener();
        PlatformUI.getWorkbench().getDisplay().addFilter(SWT.MouseDown,
                mouseListener);
    }

    @Override
    public boolean close() {
        PlatformUI.getWorkbench().getDisplay().removeFilter(SWT.MouseDown,
                mouseListener);
        return super.close();
    }

    @Override
    protected void configurePane(Composite pane) {
        Composite main = new Composite(pane, SWT.NO_FOCUS);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.TOP)
                .span(2, 1).applyTo(main);
        mainLayout = new StackLayout();
        main.setLayout(mainLayout);

        notificationsList = createListArea(main);
        mainLayout.topControl = notificationsList;

        createToolBar(pane);
    }

    private void createToolBar(Composite parent) {
        createToolbar(parent, SWT.LEFT, new Login());
        createToolbar(parent, SWT.RIGHT, new ClearAll());
    }

    private NotificationsListComposite createListArea(Composite pane) {
        NotificationsListComposite composite = new NotificationsListComposite(
                pane);
        return composite;
    }

    private void createToolbar(Composite parent, int alignment, Action action) {
        ToolBarManager toolBarManager = new ToolBarManager(
                SWT.FLAT | SWT.HORIZONTAL | SWT.NO_FOCUS);
        ToolBar toolBar = toolBarManager.createControl(parent);
        GridDataFactory.fillDefaults().align(alignment, SWT.BOTTOM)
                .applyTo(toolBar);
        toolBarManager.add(action);
        toolBarManager.update(true);
    }

    private class ClickListener implements Listener {

        public void handleEvent(Event event) {
            Point location = event.widget.getDisplay().getCursorLocation();
            if (!getShell().getBounds().contains(location)) {
                close();
            }
        }
    }

    private class Login extends Action {

        Login() {
            // TODO: I18N!
            setText("Sign In");
        }

        @Override
        public void run() {
            mainLayout.topControl = new SignInComposite(
                    notificationsList.getParent());
            notificationsList.getParent().layout();
        }

    }

    private class ClearAll extends Action {

        ClearAll() {
            // TODO: I18N!
            setText("Clear All");
        }

        @Override
        public void run() {
        }
    }
}
