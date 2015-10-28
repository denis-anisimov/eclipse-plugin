package com.vaadin.integration.eclipse.notifications.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

public class StatisticsJob extends Job {

    public enum Action {
        DETAILS_REQUEST, READ_MORE;
    }

    private final Action action;
    private final String token;
    private final String id;

    public StatisticsJob(String token, String notificationId, Action action) {
        // TODO: I18N
        super("Usage statistics");
        setUser(false);
        setSystem(true);

        this.token = token;
        this.action = action;
        id = notificationId;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        // TODO :I18N
        monitor.beginTask("Sending usage statistics", 1);

        try {
            switch (action) {
            case DETAILS_REQUEST:
                NotificationsService.getInstance().detailsRequested(token, id);
                break;
            case READ_MORE:
                NotificationsService.getInstance().infoRequested(token, id);
                break;
            }
        } finally {
            monitor.worked(1);
            monitor.done();
        }

        return Status.OK_STATUS;
    }

}
