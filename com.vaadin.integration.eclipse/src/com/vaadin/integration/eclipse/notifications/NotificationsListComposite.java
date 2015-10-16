package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

class NotificationsListComposite extends ScrolledComposite
        implements Listener, DisposeListener {

    private final PopupUpdateManager updateManager;

    NotificationsListComposite(Composite parent, PopupUpdateManager manager) {
        super(parent, SWT.NO_FOCUS | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        this.updateManager = manager;

        setExpandHorizontal(true);
        setExpandVertical(true);

        Composite composite = new Composite(this, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);
        setContent(composite);

        initComponents(composite);
        setMinSize(composite.computeSize(0, SWT.DEFAULT));

        parent.getDisplay().addFilter(SWT.MouseDown, this);
        parent.getShell().addDisposeListener(this);
    }

    public void handleEvent(Event event) {
        Composite root = (Composite) getChildren()[0];
        if (isControlClicked(this)) {
            for (Control child : root.getChildren()) {
                AbstractNotificationItem item = (AbstractNotificationItem) child;
                if (isControlClicked(item)) {
                    item.runAction(updateManager);
                    break;
                }
            }
        }
    }

    public void widgetDisposed(DisposeEvent e) {
        getParent().getDisplay().removeFilter(SWT.MouseDown,
                NotificationsListComposite.this);
    }

    private void initComponents(Composite parent) {
        if (!isSignedIn()) {
            SignInItem item = new SignInItem(parent);
            setLayoutData(item);
        }
    }

    private boolean isControlClicked(Control control) {
        Point location = control.getDisplay().getCursorLocation();
        Point listLocation = control.toDisplay(0, 0);
        Point size = control.getSize();
        Rectangle bounds = new Rectangle(listLocation.x, listLocation.y, size.x,
                size.y);
        return bounds.contains(location);
    }

    private void setLayoutData(Control control) {
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.FILL).applyTo(control);
    }

    private boolean isSignedIn() {
        // TODO
        return false;
    }
}
