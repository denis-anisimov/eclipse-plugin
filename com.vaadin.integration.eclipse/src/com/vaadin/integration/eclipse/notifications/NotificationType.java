package com.vaadin.integration.eclipse.notifications;

import org.eclipse.swt.graphics.Image;

import com.vaadin.integration.eclipse.VaadinPlugin;

enum NotificationType {

    // TODO: icon for several notifications type!!!!
    SIGN_IN(Utils.SIGN_IN_ICON, null), FEW_NOTIFICATIONS(Utils.SIGN_IN_ICON,
            null), WEBINAR(null, null), VAADIN(null, null);
    private final String iconId;
    private final String backgroundId;

    NotificationType(String iconId, String backgroundId) {
        this.iconId = iconId;
        this.backgroundId = backgroundId;
    }

    public Image getIcon() {
        return VaadinPlugin.getInstance().getImageRegistry().get(iconId);
    }

    public Image getBackground() {
        return VaadinPlugin.getInstance().getImageRegistry().get(backgroundId);
    }
}
