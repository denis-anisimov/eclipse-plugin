package com.vaadin.integration.eclipse.notifications;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.NotificationsContribution.ContributionControlAccess;
import com.vaadin.integration.eclipse.notifications.Utils.UrlOpenException;
import com.vaadin.integration.eclipse.notifications.jobs.FetchNotificationsJob;
import com.vaadin.integration.eclipse.notifications.jobs.MarkReadJob;
import com.vaadin.integration.eclipse.notifications.jobs.NewNotificationsJob;
import com.vaadin.integration.eclipse.notifications.jobs.SignInJob;
import com.vaadin.integration.eclipse.notifications.jobs.StatisticsJob;
import com.vaadin.integration.eclipse.notifications.jobs.ValidationJob;
import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.SignInNotification;
import com.vaadin.integration.eclipse.preferences.PreferenceConstants;

/**
 * Provides an entry point to manage notifications plugged functionality.
 * 
 * This class is not thread safe. All methods have to be called inside SWT UI
 * thread. This is done intentionally: all backend jobs which can and should use
 * not UI threads are created and managed by this class only.
 * 
 * Any other Notification class (except backend) is intended to be used only in
 * SWT UI and most not produce any jobs/threads by itself.
 *
 */
public final class ContributionService extends ContributionControlAccess {

    enum PopupViewMode {
        LIST, NOTIFICATION, SIGN_IN, TOKEN;
    }

    private static final ContributionService INSTANCE = new ContributionService();

    private static final String PNG = ".png";

    private List<Notification> notifications;

    private SignInNotification signIn = new SignInNotification();

    private boolean isEmbeddedBrowserAvaialble = checkBrowserSupport();

    private static final Logger LOG = Logger
            .getLogger(ContributionService.class.getName());

    private Notification selectedNotification;

    private PopupViewMode mode;

    private WeakReference<Job> currentPollingJob;

    private WeakReference<IWebBrowser> browserView;

    static {
        loadNotificationIcons();
    }

    private ContributionService() {
        mode = PopupViewMode.LIST;
        notifications = Collections.emptyList();

        currentPollingJob = new WeakReference<Job>(null);

        VaadinPlugin.getInstance().getPreferenceStore()
                .addPropertyChangeListener(new FeatureEnableListener());
    }

    static ContributionService getInstance() {
        return INSTANCE;
    }

    Collection<Notification> getNotifications() {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;
        return notifications;
    }

    SignInNotification getSignInNotification() {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        if (isSignedIn()) {
            return null;
        }
        return signIn;
    }

    void login(String mail, String pwd, Consumer<Boolean> resultConsumer) {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        SignInJob job = new SignInJob(
                new TokenConsumer(PlatformUI.getWorkbench().getDisplay(),
                        resultConsumer),
                mail, pwd);
        job.schedule();
    }

    void validateToken(String token, Consumer<Boolean> callback) {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        ValidationJob job = new ValidationJob(
                new ValidationConsumer(PlatformUI.getWorkbench().getDisplay(),
                        token, callback),
                token);
        job.schedule();
    }

    void signIn(Runnable callback) {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        VaadinPlugin.getInstance().getPreferenceStore().setValue(
                PreferenceConstants.NOTIFICATIONS_USER_TOKEN, getToken());
        refreshNotifications(callback);
    }

    void signOut(Runnable callback) {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        VaadinPlugin.getInstance().getPreferenceStore()
                .setValue(PreferenceConstants.NOTIFICATIONS_USER_TOKEN, "");
        refreshNotifications(callback);
    }

    void markRead(Notification notification) {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        selectedNotification = notification;
        updateContributionControl();
        new MarkReadJob(getToken(), notification.getId()).schedule();
    }

    void setReadAll() {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;
        updateContributionControl();
        new MarkReadJob(getToken()).schedule();
    }

    void notificationLaunched(Notification notification) {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        if (isStatisticsEnabled()) {
            new StatisticsJob(getToken(), notification.getId()).schedule();
        }
    }

    void refreshNotifications(Runnable runnable) {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        LOG.info("Schedule fetching all notifications");
        boolean useCached = runnable == null && fetchOnStart();
        FetchNotificationsJob job = new FetchNotificationsJob(
                new AllNotificationsConsumer(
                        PlatformUI.getWorkbench().getDisplay()),
                new AnonymousTokenConsumer(
                        PlatformUI.getWorkbench().getDisplay()),
                getToken(), useCached);
        currentPollingJob = new WeakReference<Job>(job);
        job.addJobChangeListener(new JobListener(
                PlatformUI.getWorkbench().getDisplay(), runnable));
        job.schedule();
    }

    void initializeContribution() {
        if (notifications.isEmpty()) {
            refreshNotifications(null);
        } else {
            updateContributionControl();
        }
    }

    boolean isEmbeddedBrowserAvailable() {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        return isEmbeddedBrowserAvaialble;
    }

    boolean isSignedIn() {
        return getUserToken() != null && !getUserToken().isEmpty();
    }

    Notification getSelectedNotification() {
        return selectedNotification;
    }

    void setViewMode(PopupViewMode viewMode) {
        mode = viewMode;
        if (!PopupViewMode.NOTIFICATION.equals(viewMode)) {
            selectedNotification = null;
        }
    }

    PopupViewMode getViewMode() {
        return mode;
    }

    void openSignInUrl() throws UrlOpenException {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;

        IWebBrowser browser = Utils.openUrl(Utils.SIGN_IN_URL);
        browserView = new WeakReference<IWebBrowser>(browser);
    }

    IWebBrowser getBrowserView() {
        return browserView == null ? null : browserView.get();
    }

    private boolean fetchOnStart() {
        return VaadinPlugin.getInstance().getPreferenceStore()
                .getBoolean(PreferenceConstants.NOTIFICATIONS_FETCH_ON_START)
                && VaadinPlugin.getInstance().getPreferenceStore()
                        .getBoolean(PreferenceConstants.NOTIFICATIONS_ENABLED);
    }

    private boolean isStatisticsEnabled() {
        return VaadinPlugin.getInstance().getPreferenceStore()
                .getBoolean(PreferenceConstants.NOTIFICATIONS_STAT_ENABLED)
                && VaadinPlugin.getInstance().getPreferenceStore()
                        .getBoolean(PreferenceConstants.NOTIFICATIONS_ENABLED);
    }

    private String getToken() {
        return getUserToken() == null ? getAnonymousToken() : getUserToken();
    }

    private String getUserToken() {
        return VaadinPlugin.getInstance().getPreferenceStore()
                .getString(PreferenceConstants.NOTIFICATIONS_USER_TOKEN);
    }

    private String getAnonymousToken() {
        return VaadinPlugin.getInstance().getPreferenceStore()
                .getString(PreferenceConstants.NOTIFICATIONS_ANONYMOUS_TOKEN);
    }

    private void setAnonymousToken(String token) {
        VaadinPlugin.getInstance().getPreferenceStore().setValue(
                PreferenceConstants.NOTIFICATIONS_ANONYMOUS_TOKEN, token);
    }

    private void schedulePollingJob(Display display) {
        // This method has to be called inside SWT UI thread.
        assert Display.getCurrent() != null;
        LOG.info("Schedule fetching new notifications");

        if (isNotificationsEnabled()) {
            Set<String> existingIds = new HashSet<String>();
            for (Notification notification : getNotifications()) {
                existingIds.add(notification.getId());
            }

            NewNotificationsJob job = new NewNotificationsJob(
                    new AllNotificationsConsumer(display),
                    new NewNotificationsConsumer(display), existingIds,
                    getToken());
            currentPollingJob = new WeakReference<Job>(job);
            job.addJobChangeListener(new JobListener(
                    PlatformUI.getWorkbench().getDisplay(), null));
            job.schedule(getPollingInterval());
        }
    }

    private boolean isNotificationsEnabled() {
        return VaadinPlugin.getInstance().getPreferenceStore()
                .getBoolean(PreferenceConstants.NOTIFICATIONS_ENABLED);
    }

    private int getPollingInterval() {
        return VaadinPlugin.getInstance().getPreferenceStore().getInt(
                PreferenceConstants.NOTIFICATIONS_POLLING_INTERVAL) * 1000;
    }

    private void setNotifications(Collection<Notification> notifications) {
        this.notifications = new ArrayList<Notification>(notifications);
    }

    private boolean checkBrowserSupport() {
        if (PlatformUI.getWorkbench().getBrowserSupport()
                .isInternalWebBrowserAvailable()) {
            Shell shell = new Shell();
            try {
                Browser browser = new Browser(shell, SWT.NONE);
                browser.dispose();
                shell.dispose();
                return true;
            } catch (SWTError e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private static void loadNotificationIcons() {
        registerIcon(Utils.REGULAR_NOTIFICATION_ICON);
        registerIcon(Utils.NEW_NOTIFICATION_ICON);
        registerIcon(Utils.GO_ICON);
        registerIcon(Utils.RETURN_ICON);
        registerIcon(Utils.CLEAR_ALL_ICON);
        registerIcon(Utils.NEW_ICON);
        registerIcon(Utils.SIGN_IN_BUTTON);
        registerIcon(Utils.SIGN_IN_ICON);
        registerIcon(Utils.NEW_NOTIFICATIONS_ICON);
    }

    private static void registerIcon(String id) {
        IPath path = new Path(id.replace('.', '/') + PNG);
        URL url = FileLocator.find(Platform.getBundle(VaadinPlugin.PLUGIN_ID),
                path, null);
        ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
        VaadinPlugin.getInstance().getImageRegistry().put(id, descriptor);
    }

    private abstract static class AbstractConsumer<T>
            implements Consumer<T>, Runnable {

        private final AtomicReference<T> ref;
        private final Display display;

        AbstractConsumer(Display display) {
            this.display = display;
            ref = new AtomicReference<T>();
        }

        public void run() {
            handleData(ref.get());
            ref.set(null);
        }

        public void accept(T notifications) {
            ref.set(notifications);
            if (!display.isDisposed()) {
                display.asyncExec(this);
            }
        }

        protected abstract void handleData(T data);

    }

    private final class TokenConsumer extends AbstractConsumer<String> {

        private final Consumer<Boolean> callback;

        TokenConsumer(Display display, Consumer<Boolean> successCallback) {
            super(display);
            callback = successCallback;
        }

        @Override
        protected void handleData(String token) {
            if (token == null) {
                callback.accept(false);
            } else {
                VaadinPlugin.getInstance().getPreferenceStore().setValue(
                        PreferenceConstants.NOTIFICATIONS_USER_TOKEN, token);
                callback.accept(true);
            }
        }

    }

    private final class ValidationConsumer extends AbstractConsumer<Boolean> {

        private final String token;
        private final Consumer<Boolean> callback;

        ValidationConsumer(Display display, String token,
                Consumer<Boolean> successCallback) {
            super(display);
            this.token = token;
            callback = successCallback;
        }

        @Override
        protected void handleData(Boolean success) {
            if (success) {
                VaadinPlugin.getInstance().getPreferenceStore().setValue(
                        PreferenceConstants.NOTIFICATIONS_USER_TOKEN, token);
                callback.accept(true);
            } else {
                callback.accept(false);
            }
        }

    }

    private final class AnonymousTokenConsumer
            extends AbstractConsumer<String> {

        AnonymousTokenConsumer(Display display) {
            super(display);
        }

        @Override
        protected void handleData(String token) {
            setAnonymousToken(token);
        }

    }

    private final class AllNotificationsConsumer
            extends AbstractConsumer<Collection<Notification>> {

        AllNotificationsConsumer(Display display) {
            super(display);
        }

        @Override
        protected void handleData(Collection<Notification> notifications) {
            setNotifications(notifications);
            updateContributionControl();
        }

    }

    private final class NewNotificationsConsumer
            extends AbstractConsumer<Collection<Notification>> {

        NewNotificationsConsumer(Display display) {
            super(display);
        }

        @Override
        protected void handleData(Collection<Notification> collection) {
            Control control = ContributionService.getInstance()
                    .getContributionControl();
            if (control == null || control.isDisposed()) {
                return;
            }
            if (isPopupEnabled()) {
                if (collection.size() == 1) {
                    NewNotificationPopup popup = new NewNotificationPopup(
                            collection.iterator().next());
                    popup.open();
                } else if (!collection.isEmpty()) {
                    NewNotificationsPopup popup = new NewNotificationsPopup(
                            collection);
                    popup.open();
                }
            }
        }

        private boolean isPopupEnabled() {
            return VaadinPlugin.getInstance().getPreferenceStore()
                    .getBoolean(PreferenceConstants.NOTIFICATIONS_POPUP_ENABLED)
                    && VaadinPlugin.getInstance().getPreferenceStore()
                            .getBoolean(
                                    PreferenceConstants.NOTIFICATIONS_ENABLED);
        }
    }

    private final class JobListener extends JobChangeAdapter
            implements Runnable {

        private final Display display;
        private final Runnable callback;
        private final AtomicReference<Job> job;

        JobListener(Display display, Runnable runnable) {
            this.display = display;
            callback = runnable;
            job = new AtomicReference<Job>(null);
        }

        @Override
        public void done(IJobChangeEvent event) {
            if (!display.isDisposed()) {
                job.set(event.getJob());
                display.asyncExec(this);
                event.getJob().removeJobChangeListener(this);
            }
        }

        public void run() {
            if (callback == null) {
                // Polling should be scheduled only on initial notification
                // fetching. The job is initiated by UI action when callback is
                // available
                if (job != null && job.get().equals(currentPollingJob.get())) {
                    // the check above will prevent rescheduling polling if the
                    // job has been cancelled because of preferences
                    schedulePollingJob(display);
                }
            } else {
                callback.run();
            }
        }

    }

    private final class FeatureEnableListener
            implements IPropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            // This method has to be called inside SWT UI thread.
            assert Display.getCurrent() != null;

            if (PreferenceConstants.NOTIFICATIONS_ENABLED
                    .equals(event.getProperty())) {
                if (isNotificationsEnabled()) {
                    schedulePollingJob(PlatformUI.getWorkbench().getDisplay());
                } else {
                    Job job = currentPollingJob.get();
                    if (job != null) {
                        job.cancel();
                    }
                }
            }
        }
    }

}