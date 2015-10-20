package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Notification item to show it in the list of notifications.
 *
 */
class NotificationIem extends AbstractNotificationItem implements ItemAction {

    // TODO: notification info ?
    NotificationIem(Composite parent, boolean read, NotificationType type) {
        super(parent, read, type);
    }

    @Override
    protected Control createInfoSection() {
        // TODO create composite component based on notification info
        return null;
    }

    public void runAction(PopupUpdateManager manager) {
        setRead();
        manager.showNotification();
    }

}
