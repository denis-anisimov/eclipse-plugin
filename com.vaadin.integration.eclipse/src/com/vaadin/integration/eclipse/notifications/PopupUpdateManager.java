package com.vaadin.integration.eclipse.notifications;

import org.eclipse.ui.browser.IWebBrowser;

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
     * 
     * @param browser
     *            instance which has been used to open login web page. Can be
     *            null. The parameter is optional.
     */
    void showTokenInput(IWebBrowser browser);
}
