package com.vaadin.integration.eclipse.notifications.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.vaadin.integration.eclipse.notifications.Consumer;
import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

public class ValidationJob extends AbstractNotificationJob<Boolean> {

    private final String token;

    public ValidationJob(Consumer<Boolean> consumer, String token) {
        // TODO: I18N
        super("Token validation", consumer);
        setUser(false);
        setSystem(true);

        this.token = token;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        // TODO :I18N
        monitor.beginTask("Validating token", 1);

        boolean isValid = false;
        try {
            isValid = NotificationsService.getInstance().validate(token);
        } finally {
            getConsumer().accept(isValid);
            monitor.worked(1);
            monitor.done();
        }

        return Status.OK_STATUS;
    }

}
