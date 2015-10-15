package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.mylyn.commons.workbench.AbstractWorkbenchNotificationPopup;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;

import com.vaadin.integration.eclipse.VaadinPlugin;

/**
 * Shows a list of all notifications and allows to navigate to the specific
 * notification.
 * 
 * @author denis
 *
 */
class NotificationsPopup extends AbstractWorkbenchNotificationPopup {

    private static final int TITLE_HEIGHT = 24;

    private static final int MIN_HEIGHT = 100;
    private static final int MAX_WIDTH = 400;
    private static final int PADDING_EDGE = 5;

    private Composite notificationsList;

    private Listener mouseListener = new ClickListener();

    private StackLayout mainLayout;

    private Composite signOutWidget;

    private Control masterControl;

    NotificationsPopup(Control control) {
        super(control.getDisplay());
        masterControl = control;
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
    protected Control createContents(Composite parent) {
        Control content = super.createContents(parent);
        // reset gradient background image
        ((Composite) content).setBackgroundMode(SWT.INHERIT_NONE);
        return content;
    }

    @Override
    protected void createTitleArea(Composite parent) {
        ((GridData) parent.getLayoutData()).heightHint = TITLE_HEIGHT;

        Label titleImageLabel = new Label(parent, SWT.NONE);
        titleImageLabel.setImage(getPopupShellImage(TITLE_HEIGHT));

        Label titleTextLabel = new Label(parent, SWT.NONE);
        // TODO
        titleTextLabel.setText("Vaadin Notification");
        titleTextLabel.setFont(CommonFonts.BOLD);
        titleTextLabel.setForeground(getTitleForeground());
        titleTextLabel
                .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        titleTextLabel.setCursor(
                parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        createClearAll(parent);
    }

    @Override
    protected Image getPopupShellImage(int maximumHeight) {
        // TODO
        return VaadinPlugin.getInstance().getImageRegistry()
                .get(NotificationsContribution.NOTIFICATION_ICON);
    }

    @Override
    protected void createContentArea(Composite parent) {
        adjustMargins(parent);

        // composite below title
        Composite pane = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.TOP).applyTo(pane);
        pane.setLayout(gridLayout);
        gridLayout.marginWidth = 0;

        // main composite whose content is dynamic
        Composite main = new CustomComposite(pane);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.TOP)
                .span(2, 1).applyTo(main);
        mainLayout = new StackLayout();
        main.setLayout(mainLayout);

        notificationsList = createListArea(main);
        mainLayout.topControl = notificationsList;

        // toolbar bottom composite below content
        createToolBar(pane);
    }

    @Override
    protected void initializeBounds() {
        Shell shell = getShell();
        Point initialSize = shell.computeSize(MAX_WIDTH, SWT.DEFAULT);
        int height = Math.max(initialSize.y, MIN_HEIGHT);
        int width = Math.min(initialSize.x, MAX_WIDTH);

        Point location = masterControl.toDisplay(new Point(0, 0));
        Point size = new Point(width, height);
        shell.setLocation(location.x - PADDING_EDGE,
                location.y - height - PADDING_EDGE);
        shell.setSize(size);
    }

    private void createClearAll(Composite parent) {
        ScalingHyperlink link = new NotificationHyperlink(parent);
        // TODO
        link.setText("Clear All");
        link.registerMouseTrackListener();
        link.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_CLOSE));
    }

    private void adjustMargins(Composite parent) {
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.marginLeft = 0;
        layout.marginRight = 0;
    }

    private void createToolBar(Composite parent) {
        signOutWidget = createToolbar(parent, SWT.LEFT, new Login());
        signOutWidget.setVisible(false);
        createToolbar(parent, SWT.RIGHT, new ClearAll());
    }

    private NotificationsListComposite createListArea(Composite pane) {
        NotificationsListComposite composite = new NotificationsListComposite(
                pane);
        return composite;
    }

    private ToolBar createToolbar(Composite parent, int alignment,
            Action action) {
        ToolBarManager manager = new ToolBarManager(
                SWT.FLAT | SWT.HORIZONTAL | SWT.NO_FOCUS);
        ToolBar toolBar = manager.createControl(parent);
        GridDataFactory.fillDefaults().align(alignment, SWT.BOTTOM)
                .applyTo(toolBar);
        manager.add(action);
        manager.update(true);

        return toolBar;
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
            setText("Sign out");
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
            setText("Notif. settings");
        }

        @Override
        public void run() {
        }
    }

    private static class CustomComposite extends Composite {

        CustomComposite(Composite parent) {
            super(parent, SWT.NONE);
            doSetBackground(
                    getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
        }

        private void doSetBackground(Color color) {
            super.setBackground(color);
        }

        @Override
        public void setBackground(Color color) {
        }
    }
}
