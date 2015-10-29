package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.browser.IWebBrowser;

/**
 * Composite widget which accept user token to access notifications.
 *
 */
class TokenInputComposite extends Composite implements SelectionListener {

    private final PopupUpdateManager manager;
    private final IWebBrowser browser;

    private Text token;

    private Label wrongTokenLabel;

    TokenInputComposite(Composite parent, IWebBrowser browser,
            PopupUpdateManager updateManager) {
        super(parent, SWT.NONE);

        manager = updateManager;
        this.browser = browser;

        GridLayout layout = new GridLayout(1, false);
        setLayout(layout);

        initComponents();
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        ContributionService.getInstance().validateToken(token.getText().trim(),
                new ValidationCallback());
    }

    private void initComponents() {
        Label label = new Label(this, SWT.NONE);
        label.setText(Messages.Notifications_TokenViewTitle);
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.TOP).applyTo(label);

        token = new Text(this, SWT.PASSWORD | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.TOP).applyTo(token);

        Button button = new Button(this, SWT.PUSH);
        // TODO : perhaps this should be replaced by icon
        button.setText("Enter");
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(button);

        button.addSelectionListener(this);
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
}
