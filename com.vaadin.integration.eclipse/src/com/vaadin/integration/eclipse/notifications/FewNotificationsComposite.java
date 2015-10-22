package com.vaadin.integration.eclipse.notifications;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.vaadin.integration.eclipse.notifications.model.Notification;

class FewNotificationsComposite extends AbstractNotificationComposite {

    FewNotificationsComposite(Composite parent, PopupManager manager,
            Collection<Notification> notifications) {
        super(parent, new FewNotifications(), manager);
    }

    @Override
    protected void showDetails() {
        getManager().showNotificationsList();
    }

    @Override
    protected String getSummary() {
        // TODO
        return "TODO: Several notifications summary";
    }

    @Override
    protected Control buildPrefix(Composite composite) {
        Label label = new Label(composite, SWT.NONE);
        label.setText("TODO: Several notifications details");
        return label;
    }

    private static class FewNotifications extends Notification {

        FewNotifications() {
            super();
        }

    }

}
