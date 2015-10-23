package com.vaadin.integration.eclipse.notifications;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

class FetchNotificationsJob extends Job {

    private final Consumer<Collection<Notification>> consumer;

    FetchNotificationsJob(Consumer<Collection<Notification>> consumer) {
        // TODO: I18N
        super("Fetch all notifications");
        setUser(false);

        this.consumer = consumer;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        // TODO :I18N
        monitor.beginTask("Retrieve all notiifcations data", 3);

        try {
            Collection<Notification> notifications = Collections
                    .unmodifiableCollection(NotificationsService.getInstance()
                            .getAllNotifications());
            monitor.worked(1);
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            NotificationsService.getInstance().downloadIcons(notifications);
            monitor.worked(2);
            consumer.accept(notifications);
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            NotificationsService.getInstance().downloadImages(notifications);
            monitor.worked(3);
        } finally {
            monitor.done();
        }

        return Status.OK_STATUS;
    }

}
