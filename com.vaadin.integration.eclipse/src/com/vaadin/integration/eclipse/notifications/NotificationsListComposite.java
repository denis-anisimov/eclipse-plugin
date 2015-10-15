package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

class NotificationsListComposite extends ScrolledComposite {

    NotificationsListComposite(Composite parent) {
        super(parent, SWT.NO_FOCUS | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.FILL);

        setExpandHorizontal(true);
        setExpandVertical(true);

        Composite composite = new Composite(this, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);
        setContent(composite);

        initNotifications(composite);
    }

    private void initNotifications(Composite parent) {
        new Label(parent, SWT.NONE).setText("AAA");
        new Label(parent, SWT.NONE).setText("BB");
        new Label(parent, SWT.NONE).setText("CC");
        new Label(parent, SWT.NONE).setText("DD");
        new Label(parent, SWT.NONE).setText("EE");
    }

}
