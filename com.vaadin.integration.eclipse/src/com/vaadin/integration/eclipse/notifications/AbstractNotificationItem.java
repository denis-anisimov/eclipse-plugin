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

abstract class AbstractNotificationItem extends Composite implements Listener {

    private Label newNotificationLabel;

    AbstractNotificationItem(Composite parent, final boolean read,
            final NotificationType type) {
        super(parent, SWT.NONE);

        GridLayout layout = new GridLayout(4, false);
        setLayout(layout);

        setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        doSetBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        getShell().addListener(SWT.Show, new Listener() {

            public void handleEvent(Event event) {
                buildContent(read, type);
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

    protected abstract Control createInfoSection();

    protected void setRead() {
        newNotificationLabel.setImage(null);
    }

    private void doSetBackground(Color color) {
        super.setBackground(color);
    }

    private void buildContent(final boolean read, final NotificationType type) {
        getShell().removeListener(SWT.Show, this);

        initComponents(read, type);

        pack();
        layout();

        Control[] children = getParent().getChildren();
        if (children.length > 0 && children[children.length
                - 1] == AbstractNotificationItem.this) {
            getParent().layout();
        }
    }

    private void initComponents(boolean read, NotificationType type) {
        newNotificationLabel = new Label(this, SWT.NONE);
        if (!read) {
            newNotificationLabel.setImage(VaadinPlugin.getInstance()
                    .getImageRegistry().get(Utils.NEW_ICON));
        }
        GridDataFactory.fillDefaults().grab(false, true)
                .align(SWT.CENTER, SWT.CENTER).applyTo(newNotificationLabel);

        Label typeLabel = new Label(this, SWT.NONE);
        typeLabel.setImage(type.getIcon());
        GridDataFactory.fillDefaults().grab(false, true)
                .align(SWT.CENTER, SWT.CENTER).applyTo(typeLabel);

        Control infoSection = createInfoSection();
        GridDataFactory.fillDefaults().grab(true, true)
                .align(SWT.FILL, SWT.FILL).applyTo(infoSection);

        Label goLabel = new Label(this, SWT.NONE);
        goLabel.setImage(VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.GO_ICON));
        GridDataFactory.fillDefaults().grab(false, true)
                .align(SWT.RIGHT, SWT.CENTER).applyTo(typeLabel);
    }

}
