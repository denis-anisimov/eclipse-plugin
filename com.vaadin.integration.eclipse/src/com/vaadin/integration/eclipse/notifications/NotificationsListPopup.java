package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.Utils.UrlOpenException;
import com.vaadin.integration.eclipse.notifications.model.Notification;

/**
 * Shows a list of all notifications and allows to navigate to the specific
 * notification.
 * 
 * @author denis
 *
 */
class NotificationsListPopup extends AbstractPopup {

    private static final int TITLE_HEIGHT = 24;
    private static final int MAX_HEIGHT = 300;

    private final Composite nullComposite = new Composite(new Shell(),
            SWT.NONE);

    private Composite notificationsList;

    private final UpdateManagerImpl updateManager = new UpdateManagerImpl();
    private ActiveControlListener mouseListener = new ActiveControlListener();

    private StackLayout mainLayout;

    private Control signOutWidget;

    private Label titleImageLabel;

    private Label titleTextLabel;

    private Control clearAll;

    private NotificationHyperlink returnLink;

    private boolean showContent;

    NotificationsListPopup() {
        this(true);
    }

    NotificationsListPopup(boolean showContentInitially) {
        super(ContributionService.getInstance().getContributionControl()
                .getDisplay());
        setDelayClose(-1);
        showContent = showContentInitially;
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
        if (window == null || window.getShell() == null
                || window.getShell().getDisplay() == null) {
            return true;
        }
        window.getShell().getDisplay().removeFilter(SWT.MouseDown,
                mouseListener);
        window.getShell().removeListener(SWT.Resize, mouseListener);
        PlatformUI.getWorkbench().getDisplay().removeFilter(SWT.FocusOut,
                mouseListener);
        nullComposite.getShell().dispose();
        nullComposite.dispose();
        return super.close();
    }

    @Override
    public int open() {
        int result = super.open();
        activateTitle();
        return result;
    }

    @Override
    protected void createTitleArea(Composite parent) {
        ((GridData) parent.getLayoutData()).heightHint = TITLE_HEIGHT;

        titleImageLabel = new Label(parent, SWT.NONE);
        titleImageLabel.setImage(getPopupShellImage(TITLE_HEIGHT));
        titleImageLabel.setVisible(showContent);

        titleTextLabel = new Label(parent, SWT.NONE);
        // TODO
        titleTextLabel.setText("Vaadin Notification");
        titleTextLabel.setFont(CommonFonts.BOLD);
        titleTextLabel.setForeground(getTitleForeground());
        titleTextLabel
                .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        titleTextLabel.setVisible(showContent);

        clearAll = createClearAll(parent);
        clearAll.setVisible(showContent);
    }

    @Override
    protected void createContentArea(Composite parent) {
        super.createContentArea(parent);

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
        if (showContent) {
            mainLayout.topControl = notificationsList;
        } else {
            mainLayout.topControl = new Label(main, SWT.NONE);
        }

        // toolbar bottom composite below content
        createToolBar(pane);
    }

    @Override
    protected void initializeBounds() {
        Shell shell = getShell();
        Point initialSize = shell.computeSize(Utils.MAX_WIDTH, MAX_HEIGHT);
        int height = Math.min(initialSize.y, MAX_HEIGHT);
        int width = Math.min(initialSize.x, Utils.MAX_WIDTH);

        Point location = ContributionService.getInstance()
                .getContributionControl().toDisplay(new Point(0, 0));
        location.x = location.x - Utils.PADDING_EDGE;
        location.y = location.y - height - Utils.PADDING_EDGE;

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

    void open(Notification notification) {
        open();
        updateManager.showNotification(notification);
    }

    private void activateTitle() {
        titleImageLabel.setVisible(true);
        titleTextLabel.setVisible(true);
        clearAll.setVisible(true);
    }

    private Control createClearAll(Composite parent) {
        ScalingHyperlink link = new NotificationHyperlink(parent);
        // TODO
        link.setText("Clear All");
        link.registerMouseTrackListener();
        link.setImage(VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.CLEAR_ALL_ICON));
        link.addHyperlinkListener(updateManager);

        return link;
    }

    private void createToolBar(Composite parent) {
        // TODO : I18N
        signOutWidget = createAction(parent, SWT.LEFT, "Sign Out");
        signOutWidget.setVisible(false);
        createAction(parent, SWT.RIGHT, "Notif. settings");
    }

    private NotificationsListComposite createListArea(Composite pane) {
        NotificationsListComposite composite = new NotificationsListComposite(
                pane, updateManager);
        return composite;
    }

    private Control createAction(Composite parent, int alignment, String text) {
        ScalingHyperlink link = new NotificationHyperlink(parent);
        link.setText(text);
        link.registerMouseTrackListener();
        GridDataFactory.fillDefaults().align(alignment, SWT.BOTTOM)
                .applyTo(link);
        link.addHyperlinkListener(updateManager);
        return link;
    }

    private class UpdateManagerImpl extends HyperlinkAdapter
            implements PopupUpdateManager {

        public void showSignIn() {
            updateTitle();

            Composite main = mainLayout.topControl.getParent();
            mainLayout.topControl = new SignInComposite(main, updateManager);
            main.layout();

            clearAll.setVisible(false);
        }

        public void showNotification(Notification notification) {
            updateTitle();

            Composite main = mainLayout.topControl.getParent();
            mainLayout.topControl = new NotificationInfoComposite(main,
                    notification);
            main.layout();
        }

        public void showTokenInput(IWebBrowser browser) {
            Composite main = mainLayout.topControl.getParent();
            mainLayout.topControl = new TokenInputComposite(main, browser,
                    updateManager);
            main.layout();
        }

        public void showNotificationsList() {
            activateNotificationsList();
            resetNotificationsList();
        }

        @Override
        public void linkActivated(HyperlinkEvent e) {
            if (e.widget == returnLink) {
                handleReturnLink();
            } else if (e.widget == signOutWidget) {
                handleSignOut();
            } else if (e.widget == clearAll) {
                clearAll();
            } else {
                handleSettings();
            }
        }

        private void updateTitle() {
            NotificationHyperlink link = setBackLink();

            titleImageLabel.setParent(nullComposite);
            titleTextLabel.setVisible(false);
            link.getParent().layout(true);
        }

        private void clearAll() {
            if (mainLayout.topControl instanceof NotificationsListComposite) {
                ((NotificationsListComposite) mainLayout.topControl).refresh();
            }
        }

        private void handleSettings() {
            try {
                Utils.openUrl(Utils.SETTINGS_URL);
            } catch (UrlOpenException exception) {
                // TODO: open error dialog about open browser failure.
            }
        }

        private void handleSignOut() {
            // remove token
            resetNotificationsList();
            signOutWidget.setVisible(false);
        }

        private void handleReturnLink() {
            activateNotificationsList();

            Composite main = mainLayout.topControl.getParent();
            mainLayout.topControl = notificationsList;
            main.layout();
        }

        private void resetNotificationsList() {
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

            updateSignOut();
        }

        private void updateSignOut() {
            // TODO: check whether there is a token
            if (true) {
                signOutWidget.setVisible(true);
            }
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

    private class ActiveControlListener implements Listener {

        public void handleEvent(Event event) {
            if (event.widget.isDisposed()) {
                return;
            }
            Point location = event.widget.getDisplay().getCursorLocation();
            if (!getShell().isDisposed()
                    && !getShell().getBounds().contains(location)) {
                close();
            }
        }
    }

}
