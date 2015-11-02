package com.vaadin.integration.eclipse.notifications.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.vaadin.integration.eclipse.notifications.Consumer;
import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

public class FetchNotificationsJob
        extends AbstractNotificationJob<Collection<Notification>> {

    private final String token;
    private final boolean useCached;

    public FetchNotificationsJob(Consumer<Collection<Notification>> consumer,
            String token, boolean useCached) {
        super(Messages.Notifications_FetchJobName, consumer);

        this.token = token;
        this.useCached = useCached;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(Messages.Notifications_FetchTask, 3);

        try {
            int i = 1;

            Collection<Notification> notifications;
            if (useCached) {
                notifications = Collections
                        .unmodifiableCollection(NotificationsService
                                .getInstance().getCachedNotifications(token));
            } else {
                notifications = Collections
                        .unmodifiableCollection(NotificationsService
                                .getInstance().getAllNotifications(token));
            }

            // TODO: remove this. Only for testing (artificially remove the
            // last notification).
            // ============================================================
            List<Notification> temp = new ArrayList<Notification>(
                    notifications);
            temp.remove(temp.get(temp.size() - 1));
            temp.remove(temp.get(temp.size() - 1));
            notifications = temp;
            // ============================================================

            monitor.worked(i);
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
            i++;

            NotificationsService.getInstance().downloadIcons(notifications);
            monitor.worked(i);
            getConsumer()
                    .accept(Collections.unmodifiableCollection(notifications));
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            i++;
            NotificationsService.getInstance().downloadImages(notifications);
            monitor.worked(i);
        } finally {
            monitor.done();
        }

        return Status.OK_STATUS;
    }

}
