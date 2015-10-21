package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.vaadin.integration.eclipse.notifications.model.Notification;

/**
 * Notification item to show it in the list of notifications.
 *
 */
class NotificationIem extends AbstractNotificationItem implements ItemAction {

    private final Notification notification;

    NotificationIem(Composite parent, boolean read, Notification notification) {
        super(parent, notification);
        this.notification = notification;
    }

    @Override
    protected Control createInfoSection(Notification notification) {
        // TODO create composite component based on notification info
        return null;
    }

    public void runAction(PopupUpdateManager manager) {
        setRead();
        manager.showNotification(notification);
    }

}
