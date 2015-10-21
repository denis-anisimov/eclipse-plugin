package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.model.Notification;

abstract class AbstractNotificationItem extends Composite implements Listener {

    private Label newNotificationLabel;

    AbstractNotificationItem(Composite parent,
            final Notification notification) {
        super(parent, SWT.NONE);

        GridLayout layout = new GridLayout(4, false);
        setLayout(layout);

        setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        doSetBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        getShell().addListener(SWT.Show, new Listener() {

            public void handleEvent(Event event) {
                buildContent(notification);
            }

        });
    }

    @Override
    public void setBackground(Color color) {
        // Disables ability to set background outside of this class.
    }

    public void handleEvent(Event event) {
        // TODO Auto-generated method stub

    }

    protected abstract Control createInfoSection(Notification notification);

    protected void setRead() {
        newNotificationLabel.setImage(null);
    }

    private void doSetBackground(Color color) {
        super.setBackground(color);
    }

    private void buildContent(Notification notification) {
        getShell().removeListener(SWT.Show, this);

        initComponents(notification);

        pack();
        layout();

        Control[] children = getParent().getChildren();
        if (children.length > 0 && children[children.length
                - 1] == AbstractNotificationItem.this) {
            getParent().layout();
        }
    }

    private void initComponents(Notification notification) {
        newNotificationLabel = new Label(this, SWT.NONE);
        if (!notification.isRead()) {
            newNotificationLabel.setImage(VaadinPlugin.getInstance()
                    .getImageRegistry().get(Utils.NEW_ICON));
        }
        GridDataFactory.fillDefaults().grab(false, true)
                .align(SWT.CENTER, SWT.CENTER).applyTo(newNotificationLabel);

        Label typeLabel = new Label(this, SWT.NONE);
        typeLabel.setImage(notification.getIcon());
        GridDataFactory.fillDefaults().grab(false, true)
                .align(SWT.CENTER, SWT.CENTER).applyTo(typeLabel);

        Control infoSection = createInfoSection(notification);
        GridDataFactory.fillDefaults().grab(true, true)
                .align(SWT.FILL, SWT.FILL).applyTo(infoSection);

        Label goLabel = new Label(this, SWT.NONE);
        goLabel.setImage(VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.GO_ICON));
        GridDataFactory.fillDefaults().grab(false, true)
                .align(SWT.RIGHT, SWT.CENTER).applyTo(typeLabel);
    }

}
