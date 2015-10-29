package com.vaadin.integration.eclipse.notifications.jobs;

import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

public class StatisticsJob extends AbstractNotificationHandleJob {

    public StatisticsJob(String token, String notificationId) {
        super(Messages.Notifications_StatisticsJobName, token, notificationId);
    }

    @Override
    protected void handleNotification(String token, String id) {
        NotificationsService.getInstance().infoRequested(token, id);
    }

    @Override
    protected String getTaskName() {
        return Messages.Notifications_StatisticsTask;
    }

}
