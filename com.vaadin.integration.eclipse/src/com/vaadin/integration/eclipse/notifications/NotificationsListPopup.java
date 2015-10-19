package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.mylyn.commons.workbench.AbstractWorkbenchNotificationPopup;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.vaadin.integration.eclipse.VaadinPlugin;

/**
 * Shows a list of all notifications and allows to navigate to the specific
 * notification.
 * 
 * @author denis
 *
 */
class NotificationsListPopup extends AbstractWorkbenchNotificationPopup {

    private static final int TITLE_HEIGHT = 24;

    private static final int MAX_HEIGHT = 300;
    private static final int MAX_WIDTH = 400;
    private static final int PADDING_EDGE = 5;

    private final Composite nullComposite = new Composite(new Shell(),
            SWT.NONE);

    private Composite notificationsList;

    private final PopupUpdateManager updateManager = new UpdateManagerImpl();
    private ActiveControlListener mouseListener = new ActiveControlListener();

    private StackLayout mainLayout;

    private Composite signOutWidget;

    private Control masterControl;

    private Label titleImageLabel;

    private Label titleTextLabel;

    private Control clearAll;

    private NotificationHyperlink returnLink;

    NotificationsListPopup(Control control) {
        super(control.getDisplay());
        masterControl = control;
        setDelayClose(-1);
    }

    @Override
    public void create() {
        super.create();
        mouseListener = new ActiveControlListener();
        PlatformUI.getWorkbench().getDisplay().addFilter(SWT.MouseDown,
                mouseListener);
        PlatformUI.getWorkbench().getDisplay().addFilter(SWT.FocusOut,
                mouseListener);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
                .addListener(SWT.Resize, mouseListener);
    }

    @Override
    public boolean close() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        window.getShell().getDisplay().removeFilter(SWT.MouseDown,
                mouseListener);
        window.getShell().removeListener(SWT.Resize, mouseListener);
        PlatformUI.getWorkbench().getDisplay().removeFilter(SWT.FocusOut,
                mouseListener);
        nullComposite.dispose();
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

        titleImageLabel = new Label(parent, SWT.NONE);
        titleImageLabel.setImage(getPopupShellImage(TITLE_HEIGHT));

        titleTextLabel = new Label(parent, SWT.NONE);
        // TODO
        titleTextLabel.setText("Vaadin Notification");
        titleTextLabel.setFont(CommonFonts.BOLD);
        titleTextLabel.setForeground(getTitleForeground());
        titleTextLabel
                .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

        createClearAll(parent);
    }

    @Override
    protected Image getPopupShellImage(int maximumHeight) {
        return VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.REGULAR_NOTIFICATION_ICON);
    }

    @Override
    protected void createContentArea(Composite parent) {
        adjustMargins(parent);

        // composite below title
        Composite pane = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        GridDataFactory.fillDefaults().grab(true, true)
                .align(SWT.FILL, SWT.FILL).applyTo(pane);
        pane.setLayout(gridLayout);
        gridLayout.marginWidth = 0;

        // main composite whose content is dynamic
        Composite main = new Composite(pane, SWT.NO_FOCUS);
        GridDataFactory.fillDefaults().grab(true, true)
                .align(SWT.FILL, SWT.FILL).span(2, 1).applyTo(main);
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
        Point initialSize = shell.computeSize(MAX_WIDTH, MAX_HEIGHT);
        int height = Math.min(initialSize.y, MAX_HEIGHT);
        int width = Math.min(initialSize.x, MAX_WIDTH);

        Point location = masterControl.toDisplay(new Point(0, 0));
        location.x = location.x - PADDING_EDGE;
        location.y = location.y - height - PADDING_EDGE;

        Point mainWindowSize = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell().getSize();
        Point mainWindowLocation = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell().getLocation();
        if (location.x + width > mainWindowLocation.x + mainWindowSize.x) {
            location.x = mainWindowLocation.x + mainWindowSize.x - width;
        }

        Point size = new Point(width, height);
        shell.setLocation(location);
        shell.setSize(size);
    }

    private void createClearAll(Composite parent) {
        ScalingHyperlink link = new NotificationHyperlink(parent);
        // TODO
        link.setText("Clear All");
        link.registerMouseTrackListener();
        link.setImage(VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.CLEAR_ALL_ICON));

        clearAll = link;
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
                pane, updateManager);
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

    private class UpdateManagerImpl extends HyperlinkAdapter
            implements PopupUpdateManager {

        public void showSignIn() {
            NotificationHyperlink link = setBackLink();

            titleImageLabel.setParent(nullComposite);
            titleTextLabel.setVisible(false);
            link.getParent().layout(true);

            Composite main = mainLayout.topControl.getParent();
            mainLayout.topControl = new SignInComposite(main, updateManager);
            main.layout();

            clearAll.setVisible(false);
        }

        public void showNotification() {
            // TODO Auto-generated method stub

        }

        public void showTokenInput(IWebBrowser browser) {
            Composite main = mainLayout.topControl.getParent();
            mainLayout.topControl = new TokenInputComposite(main, browser,
                    updateManager);
            main.layout();
        }

        public void showNotificationsList() {
            activateNotificationsList();

            // TODO: this should show the same list (without login sign in
            // item)
            // as previously and schedule fetch notifications with logic
            // below
            // applied after notifications are fetched.
            Composite main = mainLayout.topControl.getParent();
            NotificationsListComposite newList = createListArea(main);
            mainLayout.topControl = newList;
            main.layout();
            notificationsList.dispose();
            notificationsList = newList;
        }

        @Override
        public void linkActivated(HyperlinkEvent e) {
            activateNotificationsList();

            Composite main = mainLayout.topControl.getParent();
            mainLayout.topControl = notificationsList;
            main.layout();
        }

        private void activateNotificationsList() {
            returnLink.dispose();
            returnLink = null;

            titleImageLabel.setParent(titleTextLabel.getParent());
            titleTextLabel.setVisible(true);
            titleImageLabel.moveAbove(titleImageLabel);
            titleImageLabel.getParent().layout(true);

            clearAll.setVisible(true);
        }

        private NotificationHyperlink setBackLink() {
            if (returnLink == null || returnLink.isDisposed()) {
                returnLink = new NotificationHyperlink(
                        titleImageLabel.getParent());
                // TODO : I18N
                returnLink.setText("Back to notifications");
                returnLink.setImage(VaadinPlugin.getInstance()
                        .getImageRegistry().get(Utils.RETURN_ICON));
                returnLink.moveAbove(titleImageLabel);
                returnLink.registerMouseTrackListener();
                returnLink.addHyperlinkListener(this);
            }
            return returnLink;
        }
    }

    private class ActiveControlListener implements Listener, Runnable {

        private boolean isClose;

        ActiveControlListener() {
            this(false);
        }

        ActiveControlListener(boolean close) {
            isClose = close;
        }

        public void handleEvent(Event event) {
            if (event.type == SWT.FocusOut) {
                getShell().getDisplay().asyncExec(this);
                return;
            }
            Point location = event.widget.getDisplay().getCursorLocation();
            if (!getShell().isDisposed()
                    && !getShell().getBounds().contains(location)) {
                close();
            }
        }

        public void run() {
            IWorkbenchWindow window = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow();
            if (window != null && window.getShell() != null
                    && window.getShell().getDisplay() != null
                    && PlatformUI.getWorkbench().getDisplay()
                            .getFocusControl() == null) {
                if (isClose) {
                    close();
                } else {
                    // do not close immediately if there is no focused control,
                    // schedule one more round to check
                    getShell().getDisplay()
                            .asyncExec(new ActiveControlListener(true));
                }
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

}
