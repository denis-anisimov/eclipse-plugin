package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
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

    private Text token;

    private Label wrongTokenLabel;
    private Color errorColor;
    private Font font;

    private final Listener listener;

    TokenInputComposite(Composite parent, IWebBrowser browser,
            PopupUpdateManager updateManager) {
        super(parent, SWT.NONE);

        manager = updateManager;
        this.browser = browser;
        listener = new Listener();

        GridLayout layout = new GridLayout(1, false);
        setLayout(layout);

        initComponents();
    }

    private void initComponents() {
        font = Utils.createFont(12, SWT.NORMAL, Utils.HELVETICA, Utils.ARIAL);

        Label label = new Label(this, SWT.NONE);
        label.setText(Messages.Notifications_TokenViewTitle);
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.TOP).applyTo(label);

        token = new Text(this, SWT.PASSWORD | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.TOP).applyTo(token);

        Label button = new Label(this, SWT.NONE);
        Image image = VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.SUBMIT_BUTTON);
        button.setBackgroundImage(image);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP)
                .hint(image.getImageData().width, image.getImageData().height)
                .applyTo(button);

        button.addMouseListener(listener);
        button.addMouseTrackListener(listener);
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
