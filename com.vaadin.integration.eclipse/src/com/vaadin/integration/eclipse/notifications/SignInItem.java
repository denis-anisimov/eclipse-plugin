package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

class SignInItem extends AbstractNotificationItem {

    SignInItem(Composite parent) {
        super(parent, false, NotificationType.SIGN_IN);
    }

    @Override
    protected Control createInfoSection() {
        Composite composite = new Composite(this, SWT.NONE);
        FillLayout layout = new FillLayout(SWT.VERTICAL);
        composite.setLayout(layout);

        // TODO : I18N
        new Label(composite, SWT.NONE)
                .setText("Sign in with your vaadin account");
        new Label(composite, SWT.NONE)
                .setText("See all your own notifications");

        return composite;
    }

    @Override
    void runAction(PopupUpdateManager manager) {
        manager.signIn();
    }
}
