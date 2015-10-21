package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
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

    @Override
    public void handleEvent(Event event) {
        if (Utils.isControlClicked(this)) {
            showDetails();
        }
    }

    public void widgetDisposed(DisposeEvent e) {
        getParent().getDisplay().removeFilter(SWT.MouseDown, this);
    }

    @Override
    protected Control createInfoSection(Notification notification) {
        Composite composite = new Composite(this, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        composite.setLayout(layout);

        Label title = new Label(composite, SWT.NONE);
        title.setText(getSummary());
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.FILL).applyTo(title);

        buildPrefix(composite);

        Label label = new Label(composite, SWT.NONE);
        // TODO : color
        label.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
        // TODO : I18N
        label.setText("Read more");

        return composite;
    }

    protected abstract String getSummary();

    protected abstract Control buildPrefix(Composite composite);

    protected abstract void showDetails();

    protected PopupManager getManager() {
        return manager;
    }

}
