package com.vaadin.integration.eclipse.notifications;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.NotificationsContribution.ContributionControlAccess;
import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.NotificationsService;
import com.vaadin.integration.eclipse.notifications.model.SignInNotification;

/**
 * Provides an entry point to manage notifications plugged functionality.
 *
 */
public final class ContributionService extends ContributionControlAccess {

    private static final ContributionService INSTANCE = new ContributionService();

    private static final String PNG = ".png";

    private ContributionService() {
    }

    private List<Notification> notifications;

    private SignInNotification signIn = new SignInNotification();

    private boolean isEmbeddedBrowserAvaialble = checkBrowserSupport();

    static {
        loadNotificationIcons();
    }

    public static ContributionService getInstance() {
        return INSTANCE;
    }

    public Collection<Notification> getNotifications() {
        return notifications;
    }

    public SignInNotification getSignInNotification() {
        if (isSignedIn()) {
            return null;
        }
        return signIn;
    }

    public void signIn() {
        // TODO
    }

    public void signOut() {

    }

    public void refreshNotifications() {
        Collection<Notification> notifications = NotificationsService
                .getInstance().getAllNotifications();
        // TODO : it has no sense here but this has to be invoked in separate
        // thread/job which should set notifications using UI thread, next
        // method should be executed outside of UI.
        setNotifications(notifications);
        NotificationsService.getInstance().downloadImages(notifications);
    }

    public void initializeContribution() {
        refreshNotifications();
    }

    public boolean isEmbeddedBrowserAvailable() {
        return isEmbeddedBrowserAvaialble;
    }

    private void setNotifications(Collection<Notification> notifications) {
        this.notifications = new ArrayList<Notification>(notifications);
    }

    private boolean isSignedIn() {
        // TODO
        return false;
    }

    private boolean checkBrowserSupport() {
        if (PlatformUI.getWorkbench().getBrowserSupport()
                .isInternalWebBrowserAvailable()) {
            Shell shell = new Shell();
            try {
                Browser browser = new Browser(shell, SWT.NONE);
                browser.dispose();
                shell.dispose();
                return true;
            } catch (SWTError e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private static void loadNotificationIcons() {
        registerIcon(Utils.REGULAR_NOTIFICATION_ICON);
        registerIcon(Utils.NEW_NOTIFICATION_ICON);
        registerIcon(Utils.GO_ICON);
        registerIcon(Utils.RETURN_ICON);
        registerIcon(Utils.CLEAR_ALL_ICON);
        registerIcon(Utils.NEW_ICON);
        registerIcon(Utils.SIGN_IN_BUTTON);
        registerIcon(Utils.SIGN_IN_ICON);
    }

    private static void registerIcon(String id) {
        IPath path = new Path(id.replace('.', '/') + PNG);
        URL url = FileLocator.find(Platform.getBundle(VaadinPlugin.PLUGIN_ID),
                path, null);
        ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
        VaadinPlugin.getInstance().getImageRegistry().put(id, descriptor);
    }

}