package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

abstract class AbstractNotificationComposite extends AbstractNotificationItem
        implements Listener, DisposeListener {

    private final PopupManager manager;

    AbstractNotificationComposite(Composite parent, NotificationType type,
            PopupManager manager) {
        super(parent, false, type);
        this.manager = manager;

        parent.getDisplay().addFilter(SWT.MouseDown, this);
        addDisposeListener(this);
    }

    public void handleEvent(Event event) {
        if (Utils.isControlClicked(this)) {
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
