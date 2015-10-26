package com.vaadin.integration.eclipse.notifications;

import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.model.Notification;

class FewNotificationsComposite extends AbstractNotificationComposite {

    private final int notificationsSize;

    FewNotificationsComposite(Composite parent, PopupManager manager,
            Collection<Notification> notifications) {
        super(parent, new FewNotifications(), manager);
        notificationsSize = notifications.size();
    }

    @Override
    protected void showDetails() {
        getManager().showNotificationsList();
    }

    @Override
    protected Control createInfoSection() {
        StyledText text = new StyledText(this, SWT.NO_FOCUS);
        text.setEditable(false);

        // TODO
        Color red = new Color(getDisplay(), 255, 0, 0);
        Color blue = new Color(getDisplay(), 0, 180, 240);

        // TODO: I18N
        String msg = "You have {0} new {1}";

        text.setText(
                MessageFormat.format(msg, notificationsSize, "notifications"));

        int numberIndex = msg.indexOf("{0}");
        int notificationsIndex = msg.indexOf("{1}");

        StyleRange styleRange = new StyleRange();
        styleRange.start = numberIndex;
        styleRange.length = Integer.toString(notificationsSize).length();
        styleRange.foreground = red;
        text.setStyleRange(styleRange);

        styleRange = new StyleRange();
        styleRange.start = notificationsIndex
                + Integer.toString(notificationsSize).length() - 3;
        styleRange.length = "notifications".length();
        styleRange.foreground = blue;
        text.setStyleRange(styleRange);

        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.LEFT, SWT.CENTER).applyTo(text);
        return text;
    }

    private static class FewNotifications extends Notification {

        FewNotifications() {
            super();
        }

        @Override
        public boolean isRead() {
            return true;
        }

        @Override
        public Image getIcon() {
            return VaadinPlugin.getInstance().getImageRegistry()
                    .get(Utils.NEW_NOTIFICATIONS_ICON);
        }
    }

}
