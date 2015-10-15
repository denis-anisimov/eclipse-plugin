package com.vaadin.integration.eclipse.notifications;

import java.net.URL;

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
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.vaadin.integration.eclipse.VaadinPlugin;

public class NotificationsContribution
        extends WorkbenchWindowControlContribution {

    static final String NOTIFICATION_ICON = "icons.notification";

    @Override
    protected Control createControl(Composite parent) {
        scheduleNotificationRequests();
        Button button = new Button(parent, SWT.PUSH | SWT.FLAT);
        button.setImage(getIcon());
        button.addSelectionListener(new ButtonListener(button));
        return button;
    }

    private void scheduleNotificationRequests() {
        // TODO
    }

    private Image getIcon() {
        Image image = VaadinPlugin.getInstance().getImageRegistry()
                .get(NOTIFICATION_ICON);
        if (image == null) {
            IPath path = new Path("icons/vaadin-icon-16.png");
            URL url = FileLocator.find(
                    Platform.getBundle(VaadinPlugin.PLUGIN_ID), path, null);
            ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
            VaadinPlugin.getInstance().getImageRegistry().put(NOTIFICATION_ICON,
                    descriptor);
            image = VaadinPlugin.getInstance().getImageRegistry()
                    .get(NOTIFICATION_ICON);
        }
        return image;
    }

    private static class ButtonListener extends SelectionAdapter {

        private final Control control;

        ButtonListener(Control control) {
            this.control = control;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            NotificationsPopup popup = new NotificationsPopup(control);
            popup.open();
        }
    }
}
