package com.vaadin.integration.eclipse.notifications;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;

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
import com.vaadin.integration.eclipse.notifications.model.SignInNotification;

public class NotificationsContribution
        extends WorkbenchWindowControlContribution {

    private Display display;

    private static NotificationsListPopup tempPopup;

    @Override
    protected Control createControl(Composite parent) {
        display = parent.getDisplay();
        Button button = new Button(parent, SWT.PUSH | SWT.FLAT);

        init(button);

        button.setImage(getRegularIcon());
        button.addSelectionListener(new ButtonListener());

        scheduleNotificationRequests(button);

        return button;
    }

    private void init(Control control) {
        ContributionService.getInstance().initializeContribution();
        ContributionControlAccess access = ContributionService.getInstance();
        access.setControl(control);
    }

    private void scheduleNotificationRequests(final Control control) {
        // TODO
        display.timerExec(5000, new Runnable() {

            public void run() {
                if (control.isDisposed()) {
                    return;
                }
                if (tempPopup == null || tempPopup.getShell() == null
                        || !tempPopup.getShell().isVisible()) {
                    if (false) {
                        new NewNotificationPopup(new SignInNotification() {

                            @Override
                            public String getTitle() {
                                return "Title";
                            }

                            @Override
                            public Date getDate() {
                                return new Date();
                            }

                        }).open();
                    } else {
                        new NewNotificationsPopup(
                                Collections.<Notification> emptyList()).open();
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

    static class ContributionControlAccess {

        private WeakReference<Control> control;

        Control getContributionControl() {
            return control.get();
        }

        private void setControl(Control control) {
            this.control = new WeakReference<Control>(control);
        }
    }

    private static class ButtonListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent e) {
            NotificationsListPopup popup = new NotificationsListPopup();
            popup.open();
            NotificationsContribution.tempPopup = popup;
        }
    }
}
