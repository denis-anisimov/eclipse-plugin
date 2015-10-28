package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.vaadin.integration.eclipse.notifications.model.Notification;

abstract class AbstractNotificationComposite extends AbstractNotificationItem
        implements Listener, DisposeListener {

    private final PopupManager manager;

    AbstractNotificationComposite(Composite parent, Notification notification,
            PopupManager manager) {
        super(parent, notification);
        this.manager = manager;

        parent.getDisplay().addFilter(SWT.MouseDown, this);
        addDisposeListener(this);
    }

    public void handleEvent(Event event) {
        if (Utils.isControlClicked(event, this)) {
            showDetails();
        }
    }

    public void widgetDisposed(DisposeEvent e) {
        getParent().getDisplay().removeFilter(SWT.MouseDown, this);
    }

    protected abstract void showDetails();

    protected PopupManager getManager() {
        return manager;
    }

}
