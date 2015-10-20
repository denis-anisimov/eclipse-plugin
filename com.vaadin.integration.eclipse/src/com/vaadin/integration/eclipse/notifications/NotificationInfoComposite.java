package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Composite which shows full info about notification.
 *
 */
class NotificationInfoComposite extends Composite {

    private final Notification notification;

    public NotificationInfoComposite(Composite parent,
            Notification notification) {
        super(parent, SWT.NONE);
        this.notification = notification;

    }

}
