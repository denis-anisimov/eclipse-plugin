package com.vaadin.integration.eclipse.notifications;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.vaadin.integration.eclipse.notifications.model.NotificationsService;
import com.vaadin.integration.eclipse.notifications.model.NotificationsService.InvalidCredentialsException;

class SignInJob extends Job {

    private final Consumer<String> consumer;
    private final String login;
    private final String passwd;

    private static final Logger LOG = Logger
            .getLogger(SignInJob.class.getName());

    /**
     * @param consumer
     *            Consumer which accepts resulting token. null value for the
     *            token means auth failure.
     * @param login
     *            Login
     * @param pwd
     *            Password
     */
    SignInJob(Consumer<String> consumer, String login, String pwd) {
        // TODO: I18N
        super("Sign In");
        setUser(false);
        setSystem(true);

        this.consumer = consumer;
        this.login = login;
        passwd = pwd;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        // TODO :I18N
        monitor.beginTask("Signing in", 1);

        String token = null;

        try {
            token = NotificationsService.getInstance().signIn(login, passwd);
        } catch (InvalidCredentialsException e) {
            LOG.log(Level.INFO, "Authentification failed", e);
        } finally {
            consumer.accept(token);
            monitor.worked(1);
            monitor.done();
        }

        return Status.OK_STATUS;
    }

}
