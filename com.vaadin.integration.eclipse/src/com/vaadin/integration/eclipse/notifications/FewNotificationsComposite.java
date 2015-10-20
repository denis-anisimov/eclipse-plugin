package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

class FewNotificationsComposite extends AbstractNotificationComposite {

    FewNotificationsComposite(Composite parent, PopupManager manager) {
        super(parent, NotificationType.FEW_NOTIFICATIONS, manager);
    }

    @Override
    protected void showDetails() {
        getManager().showNotificationsList();
    }

    @Override
    protected Control createInfoSection() {
        // TODO Auto-generated method stub

        Composite composite = new Composite(this, SWT.NONE);
        return composite;
    }

}
