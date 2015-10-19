package com.vaadin.integration.eclipse.notifications;

import java.util.UUID;

import org.eclipse.swt.graphics.FontData;

final class Utils {

    static final String REGULAR_NOTIFICATION_ICON = "icons.vaadin-icon16";
    static final String NEW_NOTIFICATION_ICON = "icons.vaadin-icon16-new";
    static final String GO_ICON = "icons.triangle-icon";
    static final String RETURN_ICON = "icons.chevron-left-icon";
    static final String CLEAR_ALL_ICON = "icons.bell-slash-icon";
    static final String NEW_ICON = "icons.dot";
    static final String SIGN_IN_BUTTON = "icons.sign-in-btn";
    static final String SIGN_IN_ICON = "icons.sign-in-icon40";
    static final String BROWSER_ID = UUID.randomUUID().toString();

    private Utils() {
    }

    public static FontData[] getModifiedFontData(FontData[] baseData,
            int height) {
        FontData[] styleData = new FontData[baseData.length];
        for (int i = 0; i < styleData.length; i++) {
            FontData base = baseData[i];
            styleData[i] = new FontData(base.getName(), height,
                    base.getStyle());
        }
        return styleData;
    }

}
