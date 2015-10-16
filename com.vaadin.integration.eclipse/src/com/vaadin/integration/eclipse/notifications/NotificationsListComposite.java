package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

class NotificationsListComposite extends ScrolledComposite {

    NotificationsListComposite(Composite parent) {
        super(parent, SWT.NO_FOCUS | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        setExpandHorizontal(true);
        setExpandVertical(true);

        Composite composite = new Composite(this, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);
        setContent(composite);

        initComponents(composite);
        setMinSize(composite.computeSize(0, SWT.DEFAULT));
    }

    private void initComponents(Composite parent) {
        if (!isSignedIn()) {
            SignInItem item = new SignInItem(parent);
            setLayoutData(item);
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
}
