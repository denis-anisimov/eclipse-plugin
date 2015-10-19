package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.graphics.Image;

import com.vaadin.integration.eclipse.VaadinPlugin;

enum NotificationType {

    SIGN_IN(Utils.SIGN_IN_ICON), WEBINAR(null), VAADIN(
            null);
    // TODO : more ?
    private final String iconId;

    NotificationType(String iconId) {
        this.iconId = iconId;
    }

    public Image getIcon() {
        return VaadinPlugin.getInstance().getImageRegistry().get(iconId);
    }
}
