package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.vaadin.integration.eclipse.VaadinPlugin;

class SignInComposite extends Composite implements DisposeListener {

    private Font titleFont;
    private Font labelsFont;

    SignInComposite(Composite parent) {
        super(parent, SWT.NO_FOCUS);

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 10;
        setLayout(layout);

        initComponenets();
    }

    public void widgetDisposed(DisposeEvent e) {
        titleFont.dispose();
        titleFont = null;
        labelsFont.dispose();
        labelsFont = null;
    }

    private void initComponenets() {
        Label title = new Label(this, SWT.NONE);
        titleFont = new Font(getDisplay(),
                Utils.getModifiedFontData(title.getFont().getFontData(), 18));
        labelsFont = new Font(getDisplay(),
                Utils.getModifiedFontData(title.getFont().getFontData(), 12));

        title.setFont(titleFont);

        getShell().addDisposeListener(this);

        // TODO : I18N
        title.setText("Sign in");
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
        // TODO
        link.setText("Sign in");
        link.setFont(CommonFonts.BOLD);
        link.registerMouseTrackListener();
        // TODO: color
        link.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(link);

        // Use hyperlink here to align text with previous hyperlink (label will
        // be shown a bit above the base line of link
        NotificationHyperlink label = new NotificationHyperlink(openId);
        // TODO: I18N
        label.setText("with Google, Facebook or Twitter");
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(label);
        label.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
    }

    private void addButton() {
        Button button = new Button(this, SWT.FLAT);
        button.setImage(VaadinPlugin.getInstance().getImageRegistry()
                .get(NotificationsContribution.SIGN_IN_BUTTON));

        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(button);
        new Label(this, SWT.NONE);
    }

    private void addPassword() {
        Label label = new Label(this, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(label);

        label.setFont(labelsFont);
        // TODO: I18N
        label.setText("Password");

        Text text = new Text(this, SWT.PASSWORD | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(text);

    }

    private void addEmail() {
        Label label = new Label(this, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(label);

        label.setFont(labelsFont);
        // TODO: I18N
        label.setText("E-mail address");

        Text text = new Text(this, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(text);
    }

}
