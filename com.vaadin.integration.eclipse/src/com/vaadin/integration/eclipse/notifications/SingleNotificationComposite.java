package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.widgets.Composite;

import com.vaadin.integration.eclipse.notifications.model.Notification;

/**
 * Standalone notification which is shown on new notification.
 *
 */
class SingleNotificationComposite extends AbstractNotificationComposite {

    private final Notification notification;

    SingleNotificationComposite(Composite parent, Notification notification,
            PopupManager manager) {
        super(parent, notification, manager);
        this.notification = notification;
    }

    @Override
    protected void showDetails() {
        getManager().openNotification(notification);
    }

}