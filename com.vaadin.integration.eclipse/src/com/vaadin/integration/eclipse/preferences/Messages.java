package com.vaadin.integration.eclipse.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = Messages.class.getPackage()
            .getName() + ".messages"; //$NON-NLS-1$

    public static String VaadinPreferences_EnableVersionUpdate;
    public static String VaadinPreferences_ExplanationText;
    public static String VaadinPreferences_NeverInform;
    public static String VaadinPreferences_NotificationsEnable;
    public static String VaadinPreferences_NotificationsFetchOnStart;
    public static String VaadinPreferences_NotificationsPopup;
    public static String VaadinPreferences_NotificationsSectionTitle;
    public static String VaadinPreferences_NotificationsFetchOnOpen;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
