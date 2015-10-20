package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Standalone notification which is shown on new notification.
 *
 */
class SingleNotificationComposite extends AbstractNotificationItem
        implements Listener, DisposeListener {

    SingleNotificationComposite(Composite parent, boolean read,
            NotificationType type) {
        super(parent, read, type);

        parent.getDisplay().addFilter(SWT.MouseDown, this);
        addDisposeListener(this);
    }

    @Override
    protected Control createInfoSection() {
        // TODO create composite component based on notification info
        Composite composite = new Composite(this, SWT.NONE);
        return composite;
    }

    public void handleEvent(Event event) {
        if (Utils.isControlClicked(this)) {
            System.out.println("aaaaaaaaaaaaaa");
        }
    }

    public void widgetDisposed(DisposeEvent e) {
        getParent().getDisplay().removeFilter(SWT.MouseDown, this);
    }

}
