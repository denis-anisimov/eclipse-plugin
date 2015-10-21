package com.vaadin.integration.eclipse.notifications;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.model.Notification;
import com.vaadin.integration.eclipse.notifications.model.NotificationsProvider;
import com.vaadin.integration.eclipse.notifications.model.SignInNotification;

public class NotificationsContribution
        extends WorkbenchWindowControlContribution {

    private static final String PNG = ".png";

    private Display display;

    private static NotificationsListPopup tempPopup;

    private final ContributionManagerImpl manager = new ContributionManagerImpl();

    @Override
    protected Control createControl(Composite parent) {
        display = parent.getDisplay();
        Button button = new Button(parent, SWT.PUSH | SWT.FLAT);

        loadNotificationIcons();
        button.setImage(getRegularIcon());

        button.addSelectionListener(new ButtonListener(button, manager));

        scheduleNotificationRequests(button);
        return button;
    }

    private void scheduleNotificationRequests(final Control control) {
        // TODO
        manager.setNotifications(
                NotificationsProvider.getInstance().getAllNotifications());
        display.timerExec(5000, new Runnable() {

            public void run() {
                if (tempPopup == null || tempPopup.getShell() == null
                        || !tempPopup.getShell().isVisible()) {
                    if (false) {
                        new NewNotificationPopup(control,
                                new SignInNotification() {

                            @Override
                            public String getTitle() {
                                return "Title";
                            }

                            @Override
                            public Date getDate() {
                                return new Date();
                            }

                        }, manager).open();
                    } else {
                        new NewNotificationsPopup(control,
                                Collections.<Notification> emptyList(), manager)
                                        .open();
                    }
                }
            }
        });

    }

    private Image getRegularIcon() {
        return VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.REGULAR_NOTIFICATION_ICON);
    }

    private Image getNewIcon() {
        return VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.REGULAR_NOTIFICATION_ICON);
    }

    private void loadNotificationIcons() {
        registerIcon(Utils.REGULAR_NOTIFICATION_ICON);
        registerIcon(Utils.NEW_NOTIFICATION_ICON);
        registerIcon(Utils.GO_ICON);
        registerIcon(Utils.RETURN_ICON);
        registerIcon(Utils.CLEAR_ALL_ICON);
        registerIcon(Utils.NEW_ICON);
        registerIcon(Utils.SIGN_IN_BUTTON);
        registerIcon(Utils.SIGN_IN_ICON);
    }

    private void registerIcon(String id) {
        IPath path = new Path(id.replace('.', '/') + PNG);
        URL url = FileLocator.find(Platform.getBundle(VaadinPlugin.PLUGIN_ID),
                path, null);
        ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
        VaadinPlugin.getInstance().getImageRegistry().put(id, descriptor);
    }

    private class ContributionManagerImpl implements ContributionManager {

        private List<Notification> notifications;

        public Collection<Notification> getNotifications() {
            return notifications;
        }

        private void setNotifications(Collection<Notification> notifications) {
            this.notifications = new ArrayList<Notification>(notifications);
        }
    }

    private static class ButtonListener extends SelectionAdapter {

        private final WeakReference<Control> control;
        private final WeakReference<ContributionManager> manager;

        ButtonListener(Control control, ContributionManager manager) {
            this.control = new WeakReference<Control>(control);
            this.manager = new WeakReference<ContributionManager>(manager);
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            NotificationsListPopup popup = new NotificationsListPopup(
                    control.get(), manager.get());
            popup.open();
            NotificationsContribution.tempPopup = popup;
        }
    }
}
