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

        Composite composite = new Composite(this, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 2;
        composite.setLayout(layout);
        setContent(composite);

        initComponents(composite);

        parent.getDisplay().addFilter(SWT.MouseDown, this);
        addDisposeListener(this);
        setMinSize(computeSize(0, SWT.DEFAULT));
    }

    public void handleEvent(Event event) {
        if (isVisible() && Utils.isControlClicked(event, this)) {
            Composite root = (Composite) getChildren()[0];
            for (Control child : root.getChildren()) {
                AbstractNotificationItem item = (AbstractNotificationItem) child;
                if (Utils.isControlClicked(event, item)) {
                    ((ItemAction) item).runAction(updateManager);
                    break;
                }
            }
        }
    }

    public void widgetDisposed(DisposeEvent e) {
        getParent().getDisplay().removeFilter(SWT.MouseDown, this);
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

}
