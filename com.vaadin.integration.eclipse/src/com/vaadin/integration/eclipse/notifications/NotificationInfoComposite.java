package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.model.Notification;

/**
 * Composite which shows full info about notification.
 *
 */
class NotificationInfoComposite extends Composite {

    private final Notification notification;

    private Font titleFont;

    private final Listener listener;

    private Font labelsFont;

    private Font footerFont;

    private Color footerColor;

    private static final String HTML_PREFIX = "<style>" + "span {"
            + "font-family:Helvetica, Arial, Sans-Serif;" + "font-size: 12px;"
            + "font-weight: light;" + "} " + "pre {"
            + "font-family: 'Courier New',Courier,monospace;"
            + "font-size:14 px;" + "}" + "</style> <span>";

    private static final String HTML_SUFFIX = "</span>";

    public NotificationInfoComposite(Composite parent,
            Notification notification) {
        super(parent, SWT.NONE);
        this.notification = notification;

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);

        initComponents();

        doSetBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        listener = new Listener();
        addDisposeListener(new Listener());
    }

    @Override
    public void setBackground(Color color) {
        // Disables ability to set background outside of this class.
    }

    private void doSetBackground(Color color) {
        super.setBackground(color);
    }

    private void initComponents() {
        Label label = new Label(this, SWT.NONE);
        label.setImage(VaadinPlugin.getInstance().getImageRegistry()
                .get(notification.getImageUrl()));
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.FILL).applyTo(label);

        Label title = new Label(this, SWT.NONE);
        title.setText(notification.getTitle());
        titleFont = new Font(getDisplay(),
                Utils.getModifiedFontData(title.getFont().getFontData(), 18));

        footerFont = new Font(getDisplay(), Utils.getModifiedFontData(
                title.getFont().getFontData(), 12, SWT.BOLD));

        footerColor = new Color(getDisplay(), 0, 180, 240);

        title.setFont(titleFont);

        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.FILL).applyTo(title);

        Control description = createDescription();
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1)
                .align(SWT.FILL, SWT.FILL).applyTo(description);

        NotificationHyperlink readMore = new NotificationHyperlink(this);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER)
                .applyTo(readMore);
        readMore.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
        readMore.setFont(footerFont);
        // TODO:
        readMore.setText("Read more:");

        NotificationHyperlink link = new NotificationHyperlink(this);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).indent(-5, 0)
                .applyTo(link);
        link.registerMouseTrackListener();
        link.setFont(footerFont);
        link.setText(notification.getLinkText() + Utils.FORWARD_SUFFIX);
        link.setForeground(footerColor);
    }

    private Control createDescription() {
        final Browser browser = new Browser(this, SWT.NO_FOCUS);
        StringBuilder builder = new StringBuilder(HTML_PREFIX);
        builder.append(notification.getDescription());
        builder.append(HTML_SUFFIX);
        browser.setText(builder.toString());
        browser.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
        return browser;
    }

    private Control createTextDescription() {
        Text description = new Text(this, SWT.NO_FOCUS | SWT.WRAP);
        description.setTouchEnabled(false);
        description.setEditable(false);
        description.setText(notification.getDescription());

        labelsFont = new Font(getDisplay(), Utils
                .getModifiedFontData(description.getFont().getFontData(), 12));

        description.setFont(labelsFont);
        // TODO : remove HTML, use TagSoup
        return description;
    }

    private class Listener extends HyperlinkAdapter implements DisposeListener {

        public void widgetDisposed(DisposeEvent e) {
            if (titleFont != null) {
                titleFont.dispose();
                titleFont = null;
                footerFont.dispose();
                footerFont = null;
                footerColor.dispose();
                footerColor = null;
            }
            if (labelsFont != null) {
                labelsFont.dispose();
                labelsFont = null;
            }
        }

        @Override
        public void linkActivated(HyperlinkEvent e) {
        }

    }
}
