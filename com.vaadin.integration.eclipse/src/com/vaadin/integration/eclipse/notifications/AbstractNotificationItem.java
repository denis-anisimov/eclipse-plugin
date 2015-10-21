package com.vaadin.integration.eclipse.notifications;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

abstract class AbstractNotificationItem extends Composite {

    private static final String DASH = " -";

    private static final DateFormat FORMAT = new SimpleDateFormat(
            "MMMMM d, yyyy");

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
                getShell().removeListener(SWT.Show, this);
                buildContent(notification);
            }

        });
    }

    @Override
    public void setBackground(Color color) {
        // Disables ability to set background outside of this class.
    }

    protected Control createInfoSection(Notification notification) {
        Composite composite = new Composite(this, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        composite.setLayout(layout);

        Label title = new Label(composite, SWT.NONE);
        title.setText(getSummary(notification));
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.FILL).applyTo(title);

        buildPrefix(composite, notification);

        Label label = new Label(composite, SWT.NONE);
        // TODO : color
        label.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
        // TODO : I18N
        label.setText("Read more");

        return composite;
    }

    protected String getSummary(Notification notification) {
        return notification.getTitle();
    }

    protected Control buildPrefix(Composite parent, Notification notification) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(getDate(notification) + DASH);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL).applyTo(label);
        return label;
    }

    protected void setRead() {
        newNotificationLabel.setImage(null);
    }

    private String getDate(Notification notification) {
        return FORMAT.format(notification.getDate());
    }

    private void doSetBackground(Color color) {
        super.setBackground(color);
    }

    private void buildContent(Notification notification) {
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
