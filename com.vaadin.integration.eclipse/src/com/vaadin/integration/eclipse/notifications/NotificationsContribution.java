package com.vaadin.integration.eclipse.notifications;

import java.lang.ref.WeakReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.model.Notification;

public class NotificationsContribution
        extends WorkbenchWindowControlContribution {

    @Override
    protected Control createControl(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.FLAT);

        init(button);

        button.setImage(getControlAccess().getRegularIcon());
        button.addSelectionListener(new ButtonListener());

        return button;
    }

    private void init(Button control) {
        ContributionService.getInstance().initializeContribution();
        getControlAccess().setControl(control);
    }

    private ContributionControlAccess getControlAccess() {
        return ContributionService.getInstance();
    }

    static class ContributionControlAccess {

        private WeakReference<Button> control;

        Button getContributionControl() {
            return control.get();
        }

        void updateContributionControl() {
            for (Notification notification : ContributionService.getInstance()
                    .getNotifications()) {
                if (!notification.isRead()) {
                    control.get().setImage(getNewIcon());
                    return;
                }
            }
            control.get().setImage(getRegularIcon());
        }

        private void setControl(Button control) {
            this.control = new WeakReference<Button>(control);
        }

        private Image getNewIcon() {
            return VaadinPlugin.getInstance().getImageRegistry()
                    .get(Utils.NEW_NOTIFICATION_ICON);
        }

        private Image getRegularIcon() {
            return VaadinPlugin.getInstance().getImageRegistry()
                    .get(Utils.REGULAR_NOTIFICATION_ICON);
        }
    }

    private static class ButtonListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent e) {
            NotificationsListPopup popup = new NotificationsListPopup();
            popup.open();
        }
    }
}
