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
     * Show recent notifications list (request its latest state and show) for
     * given {@code token}.
     */
    void showNotificationsList(String token);

    /**
     * Show provided {@code notification} (navigate to notification info view).
     */
    void showNotification(Notification notification);

    /**
     * Show token input view.
     * 
     * @param browser
     *            instance which has been used to open login web page. Can be
     *            null. The parameter is optional.
     */
    void showTokenInput(IWebBrowser browser);

    /**
     * Close the popup.
     */
    void close();

}
