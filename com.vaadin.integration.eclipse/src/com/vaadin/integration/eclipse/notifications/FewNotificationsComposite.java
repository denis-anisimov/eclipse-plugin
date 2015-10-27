package com.vaadin.integration.eclipse.notifications;

import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.model.Notification;

class FewNotificationsComposite extends AbstractNotificationComposite
        implements DisposeListener {

    private static final String SECOND_PARAM = "{1}";
    private static final String FIRST_PARAM = "{0}";
    private final int notificationsSize;

    private Color red;
    private Color blue;

    FewNotificationsComposite(Composite parent, PopupManager manager,
            Collection<Notification> notifications) {
        super(parent, new FewNotifications(), manager);
        notificationsSize = notifications.size();
    }

    @Override
    public void widgetDisposed(DisposeEvent e) {
        red.dispose();
        blue.dispose();
    }

    @Override
    protected void showDetails() {
        getManager().showNotificationsList();
    }

    @Override
    protected Control createInfoSection() {
        StyledText text = new StyledText(this, SWT.NO_FOCUS);
        text.setEditable(false);

        red = new Color(getDisplay(), 255, 0, 0);
        blue = new Color(getDisplay(), 0, 180, 240);

        // TODO: I18N
        String msg = "You have {0} new {1}";

        String notificationMsg = "notifications";

        text.setText(
                MessageFormat.format(msg, notificationsSize, notificationMsg));

        int numberIndex = msg.indexOf(FIRST_PARAM);
        int notificationsIndex = msg.indexOf(SECOND_PARAM);

        int numberLength = Integer.toString(notificationsSize).length();

        if (numberIndex != -1 || notificationsIndex != -1) {
            if (numberIndex == -1) {
                applyStyle(text, blue, notificationsIndex,
                        notificationMsg.length());
            } else if (notificationsIndex == -1) {
                applyStyle(text, red, numberIndex, numberLength);
            } else if (numberIndex < notificationsIndex) {
                applyStyle(text, red, numberIndex, numberLength);

                applyStyle(text, blue, notificationsIndex - FIRST_PARAM.length()
                        + numberLength, notificationMsg.length());
            } else {
                applyStyle(text, blue, notificationsIndex,
                        notificationMsg.length());

                applyStyle(text, red, numberIndex - FIRST_PARAM.length()
                        + notificationMsg.length(), numberLength);
            }
        }

        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.LEFT, SWT.CENTER).applyTo(text);
        return text;
    }

    private void applyStyle(StyledText text, Color color, int index,
            int length) {
        StyleRange styleRange = new StyleRange();
        styleRange.start = index;
        styleRange.length = length;
        styleRange.foreground = color;
        text.setStyleRange(styleRange);
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
