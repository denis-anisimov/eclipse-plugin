package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.vaadin.integration.eclipse.notifications.model.SignInNotification;

class SignInItem extends AbstractNotificationItem implements ItemAction {

    SignInItem(Composite parent, SignInNotification notification) {
        super(parent, notification);
    }

    @Override
    protected Control createInfoSection() {
        Composite composite = new Composite(this, SWT.NONE);
        FillLayout layout = new FillLayout(SWT.VERTICAL);
        composite.setLayout(layout);

        new Label(composite, SWT.NONE).setText(getSummary());
        buildPrefix(composite);

        return composite;
    }

    public void runAction(PopupUpdateManager manager) {
        activate();
        manager.showSignIn();
    }

    @Override
    protected String getSummary() {
        return Messages.Notifications_SignInItemUseAccount;
    }

    @Override
    protected Control buildPrefix(Composite composite) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.Notifications_SignInItemSeeYourNotifications);
        return label;
    }
}
