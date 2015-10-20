package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Shows new single notification with its details.
 * 
 * @author denis
 *
 */
class NewNotificationPopup extends AbstractNotificationPopup {

    private final Notification notification;

    NewNotificationPopup(Control control, Notification notification) {
        super(control);
        this.notification = notification;
    }

    @Override
    protected void createContentArea(Composite parent) {
        super.createContentArea(parent);

        // composite below title
        Composite pane = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.FILL).applyTo(pane);
        pane.setLayout(gridLayout);
        gridLayout.marginWidth = 0;

        buildNotificationItem(pane);
    }

    private void buildNotificationItem(Composite pane) {
        // TODO : make notification based on type and notification info
        SingleNotificationComposite control = new SingleNotificationComposite(
                pane, notification, getManager());
    }
}
