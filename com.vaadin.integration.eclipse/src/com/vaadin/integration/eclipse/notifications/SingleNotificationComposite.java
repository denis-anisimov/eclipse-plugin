package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.widgets.Composite;

import com.vaadin.integration.eclipse.notifications.model.Notification;

/**
 * Standalone notification which is shown on new notification.
 *
 */
class SingleNotificationComposite extends AbstractNotificationComposite {

    SingleNotificationComposite(Composite parent, Notification notification,
            PopupManager manager) {
        super(parent, notification, manager);
    }

    @Override
    protected void showDetails() {
        setRead();
        getManager().openNotification(getNotification());
    }

    @Override
    protected void setRead() {
        super.setRead();
        ContributionService.getInstance().markRead(getNotification());
    }

}
