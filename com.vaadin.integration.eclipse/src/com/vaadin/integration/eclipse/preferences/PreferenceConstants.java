package com.vaadin.integration.eclipse.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
    /**
     * A Boolean preference for whether Vaadin update notifications are by
     * default enabled in new projects.
     */
    public static final String UPDATE_NOTIFICATIONS_IN_NEW_PROJECTS = "notificationsInNewProjectsPreference";
    /**
     * A Boolean preference for not showing notifications about new Vaadin
     * versions even if they are enabled in the project settings.
     */
    public static final String DISABLE_ALL_UPDATE_NOTIFICATIONS = "disableAllNotificationsPreference";

    /*
     * =========================================================================
     * Notifications settings
     */

    public static final String NOTIFICATIONS_USER_TOKEN = "notificationsSignedInUserTokenPreference";

    public static final String NOTIFICATIONS_ANONYMOUS_TOKEN = "notificationsAnonymousTokenPreference";

    public static final String NOTIFICATIONS_POLLING_INTERVAL = "notificationsPollingIntervalPreference";

    public static final String NOTIFICATIONS_ENABLED = "notificationsEnabledPreference";

    public static final String NOTIFICATIONS_POPUP_ENABLED = "notificationsPopupEnabledPreference";

    public static final String NOTIFICATIONS_STAT_ENABLED = "notificationsStatisticsEnabledPreference";
}
