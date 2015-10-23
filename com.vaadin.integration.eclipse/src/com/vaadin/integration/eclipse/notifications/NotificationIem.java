package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.widgets.Composite;

import com.vaadin.integration.eclipse.notifications.model.Notification;

/**
 * Notification item to show it in the list of notifications.
 *
 */
class NotificationIem extends AbstractNotificationItem implements ItemAction {

    NotificationIem(Composite parent, Notification notification) {
        super(parent, notification);
    }

    public void runAction(PopupUpdateManager manager) {
        setRead();
        manager.showNotification(getNotification());
    }

    @Override
    protected void setRead() {
        super.setRead();
        ContributionService.getInstance().markRead(getNotification());
    }

}
