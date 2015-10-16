package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.vaadin.integration.eclipse.VaadinPlugin;

abstract class AbstractNotificationItem extends Composite {

    private Label newNotificationLabel;

    AbstractNotificationItem(Composite parent, boolean read,
            NotificationType type) {
        super(parent, SWT.NONE);

        GridLayout layout = new GridLayout(4, false);
        setLayout(layout);

        setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        initComponents(read, type);
    }

    protected abstract Control createInfoSection();

    void setRead() {
        newNotificationLabel.setImage(null);
    }

    private void initComponents(boolean read, NotificationType type) {
        newNotificationLabel = new Label(this, SWT.NONE);
        if (!read) {
            newNotificationLabel
                    .setImage(VaadinPlugin.getInstance().getImageRegistry()
                            .get(NotificationsContribution.NEW_ICON));
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
                .get(NotificationsContribution.GO_ICON));
        GridDataFactory.fillDefaults().grab(false, true)
                .align(SWT.RIGHT, SWT.CENTER).applyTo(typeLabel);
    }

}
