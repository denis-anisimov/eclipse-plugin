package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.vaadin.integration.eclipse.VaadinPlugin;

/**
 * Composite widget which accept user token to access notifications.
 *
 */
class TokenInputComposite extends Composite {

    private final PopupUpdateManager manager;
    private final IWebBrowser browser;

    private StyledText token;

    private Label wrongTokenLabel;
    private Color errorColor;
    private Font font;
    private Font inputFont;

    private final Listener listener;

    TokenInputComposite(Composite parent, IWebBrowser browser,
            PopupUpdateManager updateManager) {
        super(parent, SWT.NONE);

        manager = updateManager;
        this.browser = browser;
        listener = new Listener();

        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 7;
        layout.marginRight = 5;
        layout.marginLeft = 5;
        setLayout(layout);

        initComponents();
    }

    private void initComponents() {
        font = Utils.createFont(12, SWT.NORMAL, Utils.HELVETICA, Utils.ARIAL);
        inputFont = Utils.createFont(14, SWT.NORMAL, Utils.HELVETICA,
                Utils.ARIAL);

        createSteps();

        Label label = new Label(this, SWT.NONE);
        label.setText(Messages.Notifications_TokenViewTitle);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(label);
        label.setFont(font);

        token = new StyledText(this, SWT.PASSWORD | SWT.BORDER);
        token.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
        int topMargin = 10;
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.TOP)
                .hint(SWT.DEFAULT, Utils.FIELD_HEIGHT - topMargin).span(2, 1)
                .applyTo(token);
        token.setTopMargin(10);
        token.setLeftMargin(10);
        token.setFont(inputFont);

        Label button = new Label(this, SWT.NONE);
        Image image = VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.SUBMIT_BUTTON);
        button.setBackgroundImage(image);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).indent(0, 10)
                .span(2, 1)
                .hint(image.getImageData().width, image.getImageData().height)
                .applyTo(button);

        button.addMouseListener(listener);
        button.addMouseTrackListener(listener);
    }

    private void createSteps() {
        Label label = new Label(this, SWT.NONE);
        int i = 1;
        label.setText(i + ".");
        label.setFont(font);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL).applyTo(label);

        StyledText text = new StyledText(this, SWT.WRAP);
        text.setEditable(false);
        text.setText(
                "The Vaadin.com sign in site should open in your browser.");
        text.setFont(font);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
                .grab(true, false).applyTo(text);

        i++;
        label = new Label(this, SWT.NONE);
        label.setText(i + ".");
        label.setFont(font);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL).applyTo(label);

        Text item = new Text(this, SWT.WRAP);
        item.setEditable(false);
        item.setText(
                "Click on the Google, Facebook or Twitter logo to sign in.");
        item.setFont(font);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
                .grab(true, false).applyTo(item);

        i++;
        label = new Label(this, SWT.NONE);
        label.setText(i + ".");
        label.setFont(font);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL).applyTo(label);
        item = new Text(this, SWT.WRAP);
        item.setEditable(false);
        item.setText("Enter your credentials.");
        item.setFont(font);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
                .grab(true, false).applyTo(item);

        i++;
        label = new Label(this, SWT.NONE);
        label.setText(i + ".");
        label.setFont(font);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL).applyTo(label);
        item = new Text(this, SWT.WRAP);
        item.setEditable(false);
        item.setText(
                "Copy the authentication token after sign in, paste it below and click Submit.");
        item.setFont(font);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
                .grab(true, false).applyTo(item);

    }

    private class ValidationCallback implements Consumer<Boolean> {

        public void accept(Boolean success) {
            if (success) {
                if (browser != null) {
                    browser.close();
                }
                manager.showNotificationsList();
            } else if (wrongTokenLabel == null
                    || wrongTokenLabel.isDisposed()) {
                wrongTokenLabel = new Label(TokenInputComposite.this, SWT.NONE);
                wrongTokenLabel.setForeground(
                        getDisplay().getSystemColor(SWT.COLOR_RED));
                wrongTokenLabel.setText(Messages.Notifications_TokenErrorMsg);

                GridDataFactory.fillDefaults().grab(true, false)
                        .align(SWT.FILL, SWT.TOP).applyTo(wrongTokenLabel);
                layout();
            }
        }

    }

    private class Listener extends HyperlinkAdapter
            implements DisposeListener, MouseListener, MouseTrackListener {

        public void widgetDisposed(DisposeEvent e) {
            if (font != null) {
                font.dispose();
                font = null;
            }
            if (errorColor != null) {
                errorColor.dispose();
                errorColor = null;
            }
        }

        @Override
        public void linkActivated(HyperlinkEvent e) {
        }

        public void mouseDoubleClick(MouseEvent e) {
            Label button = (Label) e.widget;
            button.setImage(VaadinPlugin.getInstance().getImageRegistry()
                    .get(Utils.SIGN_IN_PRESSED_BUTTON));
            handleClick();
        }

        public void mouseDown(MouseEvent e) {
            final Label button = (Label) e.widget;
            button.setBackgroundImage(VaadinPlugin.getInstance()
                    .getImageRegistry().get(Utils.SUBMIT_PRESSED_BUTTON));
        }

        public void mouseUp(MouseEvent e) {
            Label button = (Label) e.widget;
            button.setBackgroundImage(VaadinPlugin.getInstance()
                    .getImageRegistry().get(Utils.SUBMIT_BUTTON));
            handleClick();
        }

        public void mouseEnter(MouseEvent e) {
            Label button = (Label) e.widget;
            button.setBackgroundImage(VaadinPlugin.getInstance()
                    .getImageRegistry().get(Utils.SUBMIT_HOVER_BUTTON));
        }

        public void mouseExit(MouseEvent e) {
            Label button = (Label) e.widget;
            button.setBackgroundImage(VaadinPlugin.getInstance()
                    .getImageRegistry().get(Utils.SUBMIT_BUTTON));
        }

        public void mouseHover(MouseEvent e) {
        }

        private void handleClick() {
            ContributionService.getInstance().validateToken(
                    token.getText().trim(), new ValidationCallback());
        }

    }
}
