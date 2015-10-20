package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Standalone notification which is shown on new notification.
 *
 */
class SingleNotificationComposite extends AbstractNotificationComposite {

    private final Notification notification;

    SingleNotificationComposite(Composite parent, Notification notification,
            PopupManager manager) {
        super(parent, notification.getType(), manager);
        this.notification = notification;

        parent.getDisplay().addFilter(SWT.MouseDown, this);
        addDisposeListener(this);
    }

    @Override
    protected Control createInfoSection() {
        // TODO create composite component based on notification info
        Composite composite = new Composite(this, SWT.NONE);
        return composite;
    }

    @Override
    protected void showDetails() {
        getManager().openNotification(notification);
    }

}
