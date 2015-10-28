package com.vaadin.integration.eclipse.notifications.jobs;

import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

public class MarkReadJob extends AbstractNotificationHandleJob {

    public MarkReadJob(String token) {
        this(token, null);
    }

    public MarkReadJob(String token, String notificationId) {
        // TODO: I18N
        super("Mark notification read", token, notificationId);
    }

    @Override
    protected void handleNotification(String token, String id) {
        if (id == null) {
            NotificationsService.getInstance().markReadAll(token);
        } else {
            NotificationsService.getInstance().markRead(token, id);
        }
    }

    @Override
    protected String getTaskName() {
        // TODO: I18N
        return "Sending 'mark read' request";
    }
}
