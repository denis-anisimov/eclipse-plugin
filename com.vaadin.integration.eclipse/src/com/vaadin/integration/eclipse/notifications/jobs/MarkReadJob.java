package com.vaadin.integration.eclipse.notifications.jobs;

import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

public class MarkReadJob extends AbstractNotificationHandleJob {

    public MarkReadJob(String token) {
        this(token, null);
    }

    public MarkReadJob(String token, String notificationId) {
        super(Messages.Notifications_MarkReadJobName, token, notificationId);
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
        return Messages.Notifications_MarkReadTask;
    }
}
