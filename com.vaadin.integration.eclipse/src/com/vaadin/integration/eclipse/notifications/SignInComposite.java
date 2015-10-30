package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.Utils.UrlOpenException;

class SignInComposite extends Composite {

    private Font titleFont;
    private Color textColor;
    private Font labelsFont;

    private Color signInColor;

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
        titleFont = Utils.createFont(18, SWT.NORMAL, Utils.HELVETICA,
                Utils.ARIAL);
        labelsFont = Utils.createFont(12, SWT.NORMAL, Utils.HELVETICA,
                Utils.ARIAL);

        textColor = new Color(getDisplay(), 70, 68, 64);
        signInColor = new Color(getDisplay(), 0, 180, 240);

        title.setFont(titleFont);
        title.setForeground(textColor);

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
                .indent(SWT.DEFAULT, 10).align(SWT.FILL, SWT.TOP)
                .applyTo(openId);

        GridLayout layout = new GridLayout(2, false);
        openId.setLayout(layout);
        layout.horizontalSpacing = 0;

        NotificationHyperlink link = new NotificationHyperlink(openId);
        link.setText(Messages.Notifications_SignIn);
        link.setFont(CommonFonts.BOLD);
        link.registerMouseTrackListener();
        link.setFont(labelsFont);
        link.setForeground(signInColor);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(link);
        link.addHyperlinkListener(listener);

        // Use hyperlink here to align text with previous hyperlink (label will
        // be shown a bit above the base line of link
        NotificationHyperlink label = new NotificationHyperlink(openId);
        label.setFont(labelsFont);
        label.setForeground(textColor);
        label.setText(Messages.Notifications_SignInWithSuffix);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(label);
        label.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
    }

    private void addButton() {
        Button button = new Button(this, SWT.FLAT);
        button.setImage(VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.SIGN_IN_BUTTON));

        button.addSelectionListener(listener);

        GridDataFactory.fillDefaults().indent(SWT.DEFAULT, 10)
                .align(SWT.LEFT, SWT.TOP).applyTo(button);
        new Label(this, SWT.NONE);
    }

    private void addPassword() {
        Label label = new Label(this, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .indent(SWT.DEFAULT, 10).align(SWT.FILL, SWT.TOP)
                .applyTo(label);

        label.setFont(labelsFont);
        label.setText(Messages.Notifications_SignInPassword);
        label.setForeground(textColor);

        passwd = new Text(this, SWT.PASSWORD | SWT.BORDER);
        passwd.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(passwd);

    }

    private void addEmail() {
        Label label = new Label(this, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .indent(SWT.DEFAULT, 5).align(SWT.FILL, SWT.TOP).applyTo(label);

        label.setFont(labelsFont);
        label.setText(Messages.Notifications_SignInEmail);
        label.setForeground(textColor);

        email = new Text(this, SWT.BORDER);
        email.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
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

    private void showTokenInput() {
        manager.showTokenInput(
                ContributionService.getInstance().getBrowserView());
    }

    private class Listener extends HyperlinkAdapter
            implements DisposeListener, SelectionListener, Consumer<Boolean> {

        public void widgetDisposed(DisposeEvent e) {
            if (titleFont != null) {
                titleFont.dispose();
                titleFont = null;
                labelsFont.dispose();
                labelsFont = null;
                textColor.dispose();
                textColor = null;
                signInColor.dispose();
                signInColor = null;
            }
        }

        @Override
        public void linkActivated(HyperlinkEvent e) {
            try {
                ContributionService.getInstance().openSignInUrl();
                showTokenInput();
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
