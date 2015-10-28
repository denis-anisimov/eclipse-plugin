package com.vaadin.integration.eclipse.notifications;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.vaadin.integration.eclipse.notifications.model.NotificationsService;

class ValidationJob extends Job {

    private final Consumer<Boolean> consumer;
    private final String token;

    ValidationJob(Consumer<Boolean> consumer, String token) {
        // TODO: I18N
        super("Token validation");
        setUser(false);
        setSystem(true);

        this.consumer = consumer;
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
            consumer.accept(isValid);
            monitor.worked(1);
            monitor.done();
        }

        return Status.OK_STATUS;
    }

}
