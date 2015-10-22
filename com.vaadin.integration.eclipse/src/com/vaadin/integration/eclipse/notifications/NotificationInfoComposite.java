package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.vaadin.integration.eclipse.notifications.model.Notification;

/**
 * Composite which shows full info about notification.
 *
 */
class NotificationInfoComposite extends Composite {

    public NotificationInfoComposite(Composite parent,
            Notification notification) {
        super(parent, SWT.NONE);
    }

}
