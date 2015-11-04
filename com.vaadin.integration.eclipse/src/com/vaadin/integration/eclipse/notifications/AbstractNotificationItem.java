package com.vaadin.integration.eclipse.notifications;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.model.Notification;

abstract class AbstractNotificationItem extends Composite {

    private static final String DASH = " -";

    private static final int ITEM_H_MARGIN = 5;

    private static final DateFormat FORMAT = new SimpleDateFormat(
            "MMMMM d, yyyy");

    private Label newNotificationLabel;

    private final Notification notification;

    private Color readMoreColor;
    private Color textColor;
    private Font font;

    private final ItemStyle style;

    private final boolean hasNewIndicator;

    protected AbstractNotificationItem(Composite parent,
            Notification notification) {
        this(parent, notification, null);
    }

    protected AbstractNotificationItem(Composite parent,
            Notification notification, boolean hasNewIndicator) {
        this(parent, notification, null, hasNewIndicator);
    }

    protected AbstractNotificationItem(Composite parent,
            Notification notification, ItemStyle style) {
        this(parent, notification, style, true);
    }

    protected AbstractNotificationItem(Composite parent,
            Notification notification, ItemStyle style,
            boolean hasNewIndicator) {
        super(parent, SWT.NONE);
        this.notification = notification;
        this.style = style;
        this.hasNewIndicator = hasNewIndicator;

        GridLayout layout = new GridLayout(4, false);
        layout.marginRight = ITEM_H_MARGIN;
        layout.marginLeft = ITEM_H_MARGIN;
        setLayout(layout);

        setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        doSetBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        addDisposeListener(new DisposeHandler());
    }

    @Override
    public void setBackground(Color color) {
        // Disables ability to set background outside of this class.
    }

    @Override
    public void setLayoutData(Object layoutData) {
        if (newNotificationLabel == null) {
            initComponents();
        }
        super.setLayoutData(layoutData);
    }

    protected final boolean hasNewIndicator() {
        return hasNewIndicator;
    }

    protected Notification getNotification() {
        return notification;
    }

    protected Control createInfoSection() {
        font = getItemFont();
        ItemStyle style = null;
        if (font == null) {
            style = new ItemStyle();
            font = style.getFont();
        }
        textColor = getItemTextColor();
        if (textColor == null) {
            if (style == null) {
                style = new ItemStyle();
            }
            textColor = style.getTextColor();
        }

        Composite composite = new Composite(this, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        composite.setLayout(layout);

        Label title = new Label(composite, SWT.NONE);
        title.setText(getSummary());
        title.setFont(font);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.CENTER).applyTo(title);
        title.setForeground(textColor);

        buildPrefix(composite);

        readMoreColor = getReadMoreColor();
        if (readMoreColor == null) {
            if (style == null) {
                style = new ItemStyle();
            }
            readMoreColor = style.getReadMoreColor();
        }
        Label label = new Label(composite, SWT.NONE);
        label.setForeground(readMoreColor);
        label.setText(Messages.Notifications_ReadMore);
        label.setFont(font);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        title.setForeground(textColor);

        return composite;
    }

    protected String getSummary() {
        return getNotification().getTitle();
    }

    protected Control buildPrefix(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(getDate(getNotification()) + DASH);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER)
                .applyTo(label);
        label.setFont(getItemFont());
        label.setForeground(getItemTextColor());
        return label;
    }

    protected void activate() {
        setRead();
    }

    protected void setRead() {
        getNotification().setRead();
        newNotificationLabel.setImage(null);
    }

    protected Font getItemFont() {
        return style == null ? font : style.getFont();
    }

    protected Color getItemTextColor() {
        return style == null ? textColor : style.getTextColor();
    }

    private Color getReadMoreColor() {
        return style == null ? readMoreColor : style.getReadMoreColor();
    }

    private String getDate(Notification notification) {
        return FORMAT.format(notification.getDate());
    }

    private void doSetBackground(Color color) {
        super.setBackground(color);
    }

    private void initComponents() {
        newNotificationLabel = new Label(this, SWT.NONE);
        if (!getNotification().isRead()) {
            newNotificationLabel.setImage(VaadinPlugin.getInstance()
                    .getImageRegistry().get(Utils.NEW_ICON));
        }
        GridDataFactory.fillDefaults().grab(false, true)
                .align(SWT.CENTER, SWT.CENTER).applyTo(newNotificationLabel);

        Label typeLabel = new Label(this, SWT.NONE);
        typeLabel.setImage(notification.getIcon());
        GridDataFactory factory = GridDataFactory.fillDefaults()
                .grab(false, true).align(SWT.CENTER, SWT.CENTER);
        if (hasNewIndicator) {
            factory.indent(5, 0);
        }
        factory.applyTo(typeLabel);

        Control infoSection = createInfoSection();
        if (infoSection.getLayoutData() == null) {
            GridDataFactory.fillDefaults().grab(true, true)
                    .align(SWT.FILL, SWT.CENTER).applyTo(infoSection);
        }

        Label goLabel = new Label(this, SWT.NONE);
        goLabel.setImage(VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.GO_ICON));
        GridDataFactory.fillDefaults().grab(false, true)
                .align(SWT.RIGHT, SWT.CENTER).applyTo(goLabel);
    }

    private class DisposeHandler implements DisposeListener {

        public void widgetDisposed(DisposeEvent e) {
            if (readMoreColor != null) {
                readMoreColor.dispose();
            }
        }

    }

    static class ItemStyle {
        private final Font font;
        private final Color color;
        private final Color readMoreColor;

        ItemStyle(Font font, Color textColor, Color readMoreColor) {
            this.font = font;
            this.color = textColor;
            this.readMoreColor = readMoreColor;
        }

        ItemStyle() {
            this(Utils.createFont(12, SWT.NORMAL, Utils.HELVETICA, Utils.ARIAL),
                    new Color(PlatformUI.getWorkbench().getDisplay(), 154, 150,
                            143),
                    new Color(PlatformUI.getWorkbench().getDisplay(), 0, 180,
                            240));
        }

        Font getFont() {
            return font;
        }

        Color getTextColor() {
            return color;
        }

        Color getReadMoreColor() {
            return readMoreColor;
        }
    }
}
