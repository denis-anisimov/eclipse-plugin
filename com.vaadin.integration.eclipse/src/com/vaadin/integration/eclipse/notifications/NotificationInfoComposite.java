package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.model.Notification;

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

        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);

        initComponents();

        doSetBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    }

    @Override
    public void setBackground(Color color) {
        // Disables ability to set background outside of this class.
    }

    private void doSetBackground(Color color) {
        super.setBackground(color);
    }

    private void initComponents() {
        Label label = new Label(this, SWT.NONE);
        label.setImage(VaadinPlugin.getInstance().getImageRegistry()
                .get(notification.getImageUrl()));
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.FILL).applyTo(label);

    }
}
