package com.vaadin.integration.eclipse.notifications;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.NotificationsContribution.ContributionControlAccess;
import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.SignInNotification;

/**
 * Provides an entry point to manage notifications plugged functionality.
 *
 */
public final class ContributionService extends ContributionControlAccess {

    private static final ContributionService INSTANCE = new ContributionService();

    private static final String PNG = ".png";

    private List<Notification> notifications;

    private SignInNotification signIn = new SignInNotification();

    private boolean isEmbeddedBrowserAvaialble = checkBrowserSupport();

    static {
        loadNotificationIcons();
    }

    private ContributionService() {
    }

    static ContributionService getInstance() {
        return INSTANCE;
    }

    Collection<Notification> getNotifications() {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;
        return notifications;
    }

    SignInNotification getSignInNotification() {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        if (isSignedIn()) {
            return null;
        }
        return signIn;
    }

    void signIn() {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;
        // TODO
    }

    void signOut() {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

    }

    void markRead(Notification notification) {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;
        updateContributionControl();
        // TODO : call REST API
    }

    void refreshNotifications() {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;
        FetchNotificationsJob job = new FetchNotificationsJob(
                new AllNotificationsConsumer(
                        PlatformUI.getWorkbench().getDisplay()));
        job.schedule();
    }

    void initializeContribution() {
        refreshNotifications();

        // TODO : remove this . Temporary code
        PlatformUI.getWorkbench().getDisplay().timerExec(5000, new Runnable() {

            public void run() {
                if (getContributionControl().isDisposed()) {
                    return;
                }
                if (false) {
                    new NewNotificationPopup(new SignInNotification() {

                        @Override
                        public String getTitle() {
                            return "Title";
                        }

                        @Override
                        public Date getDate() {
                            return new Date();
                        }

                    }).open();
                } else {
                    new NewNotificationsPopup(
                            Collections.<Notification> emptyList()).open();
                }
            }
        });
    }

    boolean isEmbeddedBrowserAvailable() {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

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

    private class AllNotificationsConsumer
            implements Consumer<Collection<Notification>>, Runnable {

        private final AtomicReference<Collection<Notification>> collection;
        private final Display display;

        AllNotificationsConsumer(Display display) {
            this.display = display;
            collection = new AtomicReference<Collection<Notification>>();
        }

        public void run() {
            setNotifications(collection.get());
            updateContributionControl();
            collection.set(null);
        }

        public void accept(Collection<Notification> notifications) {
            collection.set(notifications);
            display.asyncExec(this);
        }

    }
}