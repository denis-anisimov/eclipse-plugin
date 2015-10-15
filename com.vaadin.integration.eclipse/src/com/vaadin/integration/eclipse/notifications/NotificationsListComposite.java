package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

class NotificationsListComposite extends Composite {

    NotificationsListComposite(Composite parent) {
        super(parent, SWT.NO_FOCUS);

        setLayout(new FillLayout());
        Label button = new Label(this, SWT.NONE);
        button.setText("dsfds");
    }

}
