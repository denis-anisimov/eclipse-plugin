package com.vaadin.integration.eclipse.notifications;

interface PopupUpdateManager {

    /**
     * Shows Sign In view.
     */
    void showSignIn();

    /**
     * Show recent notifications list (request its latest state and show).
     */
    void showNotificationsList();

    // TODO : notification data as an arg
    void showNotification();
}
