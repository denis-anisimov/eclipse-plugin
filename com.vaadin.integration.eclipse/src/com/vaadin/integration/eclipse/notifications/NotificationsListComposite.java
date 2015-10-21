package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.SignInNotification;

class NotificationsListComposite extends ScrolledComposite
        implements Listener, DisposeListener {

    private final PopupUpdateManager updateManager;

    NotificationsListComposite(Composite parent, PopupUpdateManager manager) {
        super(parent, SWT.NO_FOCUS | SWT.BORDER | SWT.V_SCROLL);
        this.updateManager = manager;

        setExpandHorizontal(true);
        setExpandVertical(true);

        Composite composite = new CustomComposite(this);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 2;
        composite.setLayout(layout);
        setContent(composite);

        initComponents(composite);

        parent.getDisplay().addFilter(SWT.MouseDown, this);
        addDisposeListener(this);
    }

    public void handleEvent(Event event) {
        if (isVisible()) {
            Composite root = (Composite) getChildren()[0];
            if (Utils.isControlClicked(this)) {
                for (Control child : root.getChildren()) {
                    AbstractNotificationItem item = (AbstractNotificationItem) child;
                    if (Utils.isControlClicked(item)) {
                        ((ItemAction) item).runAction(updateManager);
                        break;
                    }
                }
            }
        }
    }

    public void widgetDisposed(DisposeEvent e) {
        getParent().getDisplay().removeFilter(SWT.MouseDown, this);
    }

    void refresh() {
        markNotificationsRead();
        Composite composite = (Composite) getContent();
        for (Control child : composite.getChildren()) {
            child.dispose();
        }
        initComponents(composite);
    }

    private void markNotificationsRead() {
        // TODO Auto-generated method stub

    }

    private void initComponents(Composite parent) {
        if (!isSignedIn()) {
            setLayoutData(new SignInItem(parent, new SignInNotification()));
        }
        for (Notification notification : updateManager.getNotifications()) {
            setLayoutData(new NotificationIem(parent, notification));
        }
    }

    private void setLayoutData(Control control) {
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.FILL).applyTo(control);
    }

    private boolean isSignedIn() {
        // TODO
        return false;
    }

    private static class CustomComposite extends Composite {

        CustomComposite(ScrolledComposite parent) {
            super(parent, SWT.NONE);
        }

        @Override
        public void layout() {
            super.layout();
            getParent().setMinSize(computeSize(0, SWT.DEFAULT));
        }

        @Override
        public ScrolledComposite getParent() {
            return (ScrolledComposite) super.getParent();
        }
    }

}
