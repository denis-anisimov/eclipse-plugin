package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.SignInNotification;

class NotificationsListComposite extends ScrolledComposite {

    NotificationsListComposite(Composite parent, PopupUpdateManager manager) {
        super(parent, SWT.NO_FOCUS | SWT.BORDER | SWT.V_SCROLL);

        setExpandHorizontal(true);
        setExpandVertical(true);

        CustomComposite composite = new CustomComposite(this, manager);
        setContent(composite);

        initComponents(composite);

        parent.getDisplay().addFilter(SWT.MouseDown, composite);
        addDisposeListener(composite);
        setMinSize(computeSize(0, SWT.DEFAULT));

        doSetBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    }

    @Override
    public void setBackground(Color color) {
        // Disables ability to set background outside of this class.
    }

    void clearAll() {
        Composite composite = (Composite) getContent();
        for (Control child : composite.getChildren()) {
            if (child instanceof AbstractNotificationItem) {
                ((AbstractNotificationItem) child).setRead();
            }
        }
        ContributionService.getInstance().setReadAll();
    }

    private void doSetBackground(Color color) {
        super.setBackground(color);
    }

    private void initComponents(Composite parent) {
        SignInNotification signIn = ContributionService.getInstance()
                .getSignInNotification();
        if (signIn != null) {
            setControlLayoutData(new SignInItem(parent, signIn));
        }
        for (Notification notification : ContributionService.getInstance()
                .getNotifications()) {
            setControlLayoutData(new NotificationIem(parent, notification));
        }
    }

    private void setControlLayoutData(Control item) {
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.FILL).applyTo(item);
    }

    private static final class CustomComposite extends Composite
            implements Listener, DisposeListener {

        private final PopupUpdateManager updateManager;

        private final Color bckgrnd;

        CustomComposite(Composite parent, PopupUpdateManager manager) {
            super(parent, SWT.NONE);
            this.updateManager = manager;

            bckgrnd = new Color(parent.getDisplay(), 225, 225, 225);
            super.setBackground(bckgrnd);

            GridLayout layout = new GridLayout(1, false);
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            layout.verticalSpacing = 2;
            setLayout(layout);
        }

        @Override
        public void setBackground(Color color) {
            // Disables ability to set background outside of this class.
        }

        public void widgetDisposed(DisposeEvent e) {
            getParent().getDisplay().removeFilter(SWT.MouseDown, this);
            bckgrnd.dispose();
        }

        public void handleEvent(Event event) {
            if (isVisible() && Utils.isControlClicked(event, this)) {
                for (Control child : getChildren()) {
                    AbstractNotificationItem item = (AbstractNotificationItem) child;
                    if (Utils.isControlClicked(event, item)) {
                        ((ItemAction) item).runAction(updateManager);
                        break;
                    }
                }
            }
        }
    }

}
