package com.vaadin.integration.eclipse.notifications.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.vaadin.integration.eclipse.notifications.Consumer;
import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

public class NewNotificationsJob
        extends AbstractNotificationJob<Collection<Notification>> {

    private final Consumer<Collection<Notification>> newNotiifcationsConsumer;

    private final Set<String> notificationIds;

    private final String token;

    public NewNotificationsJob(Consumer<Collection<Notification>> consumer,
            Consumer<Collection<Notification>> newNotiifcationsConsumer,
            Set<String> existingIds, String token) {
        super(Messages.Notifications_FetchNewJob, consumer);

        this.newNotiifcationsConsumer = newNotiifcationsConsumer;
        this.token = token;
        notificationIds = existingIds;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(Messages.Notifications_FetchNewTask, 5);

        try {
            Collection<Notification> notifications = Collections
                    .unmodifiableCollection(NotificationsService.getInstance()
                            .getAllNotifications(token));
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
            getConsumer().accept(notifications);
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
