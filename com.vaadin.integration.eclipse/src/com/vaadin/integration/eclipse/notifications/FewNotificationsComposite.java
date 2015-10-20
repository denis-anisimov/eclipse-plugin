package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

class FewNotificationsComposite extends AbstractNotificationComposite {

    FewNotificationsComposite(Composite parent, PopupManager manager) {
        super(parent, NotificationType.FEW_NOTIFICATIONS, manager);
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

}
