package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.Utils.UrlOpenException;

class SignInComposite extends Composite {

    private Font titleFont;
    private Font labelsFont;
    private final Listener listener;

    private Text email;
    private Text passwd;

    private Label loginFailedLabel;

    private PopupUpdateManager manager;

    SignInComposite(Composite parent, PopupUpdateManager updateManager) {
        super(parent, SWT.NO_FOCUS);
        manager = updateManager;

        listener = new Listener();

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 10;
        setLayout(layout);

        addDisposeListener(listener);

        initComponenets();
    }

    private void initComponenets() {
        Label title = new Label(this, SWT.NONE);
        titleFont = new Font(getDisplay(),
                Utils.getModifiedFontData(title.getFont().getFontData(), 18));
        labelsFont = new Font(getDisplay(),
                Utils.getModifiedFontData(title.getFont().getFontData(), 12));

        title.setFont(titleFont);

        title.setText(Messages.Notifications_SignIn);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(title);

        addEmail();
        addPassword();
        addButton();
        addOpenIdAction();
    }

    private void addOpenIdAction() {
        Composite openId = new Composite(this, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(openId);

        GridLayout layout = new GridLayout(2, false);
        openId.setLayout(layout);
        layout.horizontalSpacing = 0;

        NotificationHyperlink link = new NotificationHyperlink(openId);
        link.setText(Messages.Notifications_SignIn);
        link.setFont(CommonFonts.BOLD);
        link.registerMouseTrackListener();
        // TODO: color
        link.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(link);
        link.addHyperlinkListener(listener);

        // Use hyperlink here to align text with previous hyperlink (label will
        // be shown a bit above the base line of link
        NotificationHyperlink label = new NotificationHyperlink(openId);
        label.setText(Messages.Notifications_SignInWithSuffix);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(label);
        label.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
    }

    private void addButton() {
        Button button = new Button(this, SWT.FLAT);
        button.setImage(VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.SIGN_IN_BUTTON));

        button.addSelectionListener(listener);

        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(button);
        new Label(this, SWT.NONE);
    }

    private void addPassword() {
        Label label = new Label(this, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(label);

        label.setFont(labelsFont);
        label.setText(Messages.Notifications_SignInPassword);

        passwd = new Text(this, SWT.PASSWORD | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(passwd);

    }

    private void addEmail() {
        Label label = new Label(this, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(label);

        label.setFont(labelsFont);
        label.setText(Messages.Notifications_SignInEmail);

        email = new Text(this, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(email);
    }

    private void login() {
        String mail = email.getText().trim();
        String pwd = passwd.getText();
        showOperationProgress();
        ContributionService.getInstance().login(mail, pwd, listener);
    }

    private void showOperationProgress() {
        // TODO : show info label about request is in progress
    }

    private void notifyFailedLogin() {
        if (loginFailedLabel == null) {
            loginFailedLabel = new Label(this, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                    .align(SWT.FILL, SWT.TOP).applyTo(loginFailedLabel);
            loginFailedLabel.setText(Messages.Notifications_SignInError);
            loginFailedLabel
                    .setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
            layout();
        } else {
            loginFailedLabel.setVisible(true);
        }
    }

    private void showTokenInput(IWebBrowser browser) {
        manager.showTokenInput(browser);
    }

    private class Listener extends HyperlinkAdapter
            implements DisposeListener, SelectionListener, Consumer<Boolean> {

        public void widgetDisposed(DisposeEvent e) {
            if (titleFont != null) {
                titleFont.dispose();
                titleFont = null;
                labelsFont.dispose();
                labelsFont = null;
            }
        }

        @Override
        public void linkActivated(HyperlinkEvent e) {
            try {
                IWebBrowser browser = Utils.openUrl(Utils.SIGN_IN_URL);
                showTokenInput(browser);
            } catch (UrlOpenException exception) {
                // TODO: open error dialog about open browser failure.
            }
        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            login();
        }

        public void accept(Boolean success) {
            if (success) {
                manager.showNotificationsList();
                dispose();
            } else {
                passwd.setText("");
                notifyFailedLogin();
            }
        }

    }

}
