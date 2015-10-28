package com.vaadin.integration.eclipse.notifications.jobs;

import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

public class MarkReadJob extends AbstractNotificationHandleJob {

    public MarkReadJob(String token, String notificationId) {
        // TODO: I18N
        super("Mark notification read", token, notificationId);
    }

    @Override
    protected void handleNotification(String token, String id) {
        NotificationsService.getInstance().markRead(token, id);
    }

    @Override
    protected String getTaskName() {
        return "Sending 'mark read' request";
    }
}
