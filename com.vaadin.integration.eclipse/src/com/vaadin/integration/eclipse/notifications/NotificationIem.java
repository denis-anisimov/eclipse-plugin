package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.vaadin.integration.eclipse.notifications.model.Notification;

/**
 * Notification item to show it in the list of notifications.
 *
 */
class NotificationIem extends AbstractNotificationItem implements ItemAction {

    private final Notification notification;

    NotificationIem(Composite parent, Notification notification) {
        super(parent, notification);
        this.notification = notification;
    }

    public void runAction(PopupUpdateManager manager) {
        setRead();
        manager.showNotification(notification);
    }

    @Override
    protected Control buildPrefix(Composite composite,
            Notification notification) {
        Label date = new Label(composite, SWT.NONE);
        date.setText(notification.getDate().toString());
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL).applyTo(date);
        return date;
    }

}
