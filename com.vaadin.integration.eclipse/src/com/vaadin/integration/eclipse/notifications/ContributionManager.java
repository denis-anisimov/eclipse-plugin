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

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.NotificationsProvider;
import com.vaadin.integration.eclipse.notifications.model.SignInNotification;

public final class ContributionManager {

    private static final ContributionManager INSTANCE = new ContributionManager();

    private static final String PNG = ".png";

    private ContributionManager() {
    }

    private List<Notification> notifications;

    private SignInNotification signIn = new SignInNotification();

    static {
        loadNotificationIcons();
    }

    public static ContributionManager getInstance() {
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
        setNotifications(
                NotificationsProvider.getInstance().getAllNotifications());
    }

    public void initializeContribution() {
        refreshNotifications();
    }

    private void setNotifications(Collection<Notification> notifications) {
        this.notifications = new ArrayList<Notification>(notifications);
    }

    private boolean isSignedIn() {
        // TODO
        return false;
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