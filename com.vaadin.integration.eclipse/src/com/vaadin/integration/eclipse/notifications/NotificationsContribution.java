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

    private static final String PNG = ".png";
    @Override
    protected Control createControl(Composite parent) {
        scheduleNotificationRequests();
        Button button = new Button(parent, SWT.PUSH | SWT.FLAT);

        loadNotificationIcons();
        button.setImage(getRegularIcon());

        button.addSelectionListener(new ButtonListener(button));
        return button;
    }

    private void scheduleNotificationRequests() {
        // TODO
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

    private static class ButtonListener extends SelectionAdapter {

        private final Control control;

        ButtonListener(Control control) {
            this.control = control;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            NotificationsListPopup popup = new NotificationsListPopup(control);
            popup.open();
        }
    }
}
