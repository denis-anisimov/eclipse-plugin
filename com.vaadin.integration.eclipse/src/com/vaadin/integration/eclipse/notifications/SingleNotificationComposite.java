package com.vaadin.integration.eclipse.notifications;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.vaadin.integration.eclipse.notifications.model.Notification;

/**
 * Standalone notification which is shown on new notification.
 *
 */
class SingleNotificationComposite extends AbstractNotificationComposite {

    private static final String DASH = " -";

    private static final DateFormat FORMAT = new SimpleDateFormat(
            "MMMMM d, yyyy");

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

    @Override
    protected Control buildPrefix(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(getDate() + DASH);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL).applyTo(label);
        return label;
    }

    @Override
    protected String getSummary() {
        return notification.getTitle();
    }

    private String getDate() {
        return FORMAT.format(notification.getDate());
    }

}
