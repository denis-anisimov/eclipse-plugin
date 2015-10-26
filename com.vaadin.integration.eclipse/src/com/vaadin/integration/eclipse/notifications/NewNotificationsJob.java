package com.vaadin.integration.eclipse.notifications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

class NewNotificationsJob extends Job {

    private final Consumer<Collection<Notification>> consumer;
    private final Consumer<Collection<Notification>> newNotiifcationsConsumer;

    private final Set<String> notificationIds;

    NewNotificationsJob(Consumer<Collection<Notification>> consumer,
            Consumer<Collection<Notification>> newNotiifcationsConsumer,
            Set<String> existingIds) {
        // TODO: I18N
        super("Fetch new notifications");
        setUser(false);
        setSystem(true);

        this.consumer = consumer;
        this.newNotiifcationsConsumer = newNotiifcationsConsumer;
        notificationIds = existingIds;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        // TODO :I18N
        monitor.beginTask("Retrieve new notiifcations data", 5);

        try {
            Collection<Notification> notifications = Collections
                    .unmodifiableCollection(NotificationsService.getInstance()
                            .getAllNotifications());
            monitor.worked(1);
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            List<Notification> newNotifications = new ArrayList<Notification>();
            for (Notification notification : notifications) {
                if (!notificationIds.contains(notification.getId())) {
                    newNotifications.add(notification);
                }
            }

            monitor.worked(2);
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            NotificationsService.getInstance().downloadIcons(
                    Collections.unmodifiableCollection(newNotifications));
            monitor.worked(3);
            consumer.accept(notifications);
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            newNotiifcationsConsumer.accept(
                    Collections.unmodifiableCollection(newNotifications));
            monitor.worked(4);
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            NotificationsService.getInstance().downloadImages(newNotifications);
            monitor.worked(5);
        } finally {
            monitor.done();
        }

        return Status.OK_STATUS;
    }

}
