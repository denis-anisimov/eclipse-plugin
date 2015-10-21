package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.SignInNotification;

class SignInItem extends AbstractNotificationItem implements ItemAction {

    private final Notification notification;

    SignInItem(Composite parent, SignInNotification notification) {
        super(parent, notification);
        this.notification = notification;
    }

    @Override
    protected Control createInfoSection(Notification notification) {
        Composite composite = new Composite(this, SWT.NONE);
        FillLayout layout = new FillLayout(SWT.VERTICAL);
        composite.setLayout(layout);

        new Label(composite, SWT.NONE).setText(getSummary(notification));
        buildPrefix(composite, notification);

        return composite;
    }

    public void runAction(PopupUpdateManager manager) {
        setRead();
        manager.showSignIn();
    }

    @Override
    protected void setRead() {
        super.setRead();
        notification.setRead();
    }

    @Override
    protected String getSummary(Notification notification) {
        // TODO : I18N
        return "Sign in with your vaadin account";
    }

    @Override
    protected Control buildPrefix(Composite composite,
            Notification notification) {
        Label label = new Label(composite, SWT.NONE);
        // TODO : I18N
        label.setText("See all your own notifications");
        return label;
    }
}
