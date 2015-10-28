package com.vaadin.integration.eclipse.notifications.jobs;

import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

public class StatisticsJob extends AbstractNotificationHandleJob {

    public StatisticsJob(String token, String notificationId) {
        // TODO: I18N
        super("Usage statistics", token, notificationId);
    }

    @Override
    protected void handleNotification(String token, String id) {
        NotificationsService.getInstance().infoRequested(token, id);
    }

    @Override
    protected String getTaskName() {
        // TODO Auto-generated method stub
        return "Sending usage statistics";
    }

}
