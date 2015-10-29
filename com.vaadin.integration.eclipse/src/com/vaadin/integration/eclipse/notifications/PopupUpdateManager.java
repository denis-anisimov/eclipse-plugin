package com.vaadin.integration.eclipse.notifications;

import org.eclipse.ui.browser.IWebBrowser;

import com.vaadin.integration.eclipse.notifications.model.Notification;

/**
 * Manages update changes in the notifications list popup.
 *
 */
interface PopupUpdateManager {

    /**
     * Shows Sign In view.
     */
    void showSignIn();

    /**
     * Show recent notifications list (request its latest state and show).
     */
    void showNotificationsList();

    /**
     * Show provided {@code notification} (navigate to notification info view).
     */
    void showNotification(Notification notification);

    /**
     * Show token input view.
     */
    void showTokenInput(IWebBrowser browser);

    /**
     * Close the popup.
     */
    void close();

}
