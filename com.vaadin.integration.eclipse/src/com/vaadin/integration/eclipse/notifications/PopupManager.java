package com.vaadin.integration.eclipse.notifications;

/**
 * Manages popups : transitions between single notification popup and list of
 * notifications.
 *
 */
interface PopupManager {

    /**
     * Shows notification in details (closes single notification popup and opens
     * notification list popup navigated to notification).
     */
    void openNotification(Notification notification);
}
