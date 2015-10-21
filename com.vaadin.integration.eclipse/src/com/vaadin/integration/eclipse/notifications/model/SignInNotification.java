package com.vaadin.integration.eclipse.notifications.model;

import org.eclipse.swt.graphics.Image;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.Utils;

public class SignInNotification extends Notification {

    public SignInNotification() {
        super(null, null, null, null, false);
    }

    @Override
    public Image getIcon() {
        return VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.SIGN_IN_ICON);
    }
}
