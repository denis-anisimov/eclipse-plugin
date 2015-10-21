package com.vaadin.integration.eclipse.notifications;

import java.util.Collection;

import com.vaadin.integration.eclipse.notifications.model.Notification;

public interface ContributionManager {

    Collection<Notification> getNotifications();
}
