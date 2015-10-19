package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

class SignInComposite extends Composite implements DisposeListener {

    private Font titleFont;

    SignInComposite(Composite parent) {
        super(parent, SWT.NO_FOCUS);

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 10;
        setLayout(layout);

        initComponenets();
    }

    public void widgetDisposed(DisposeEvent e) {
        titleFont.dispose();
    }

    private void initComponenets() {
        Label title = new Label(this, SWT.NONE);
        titleFont = new Font(getDisplay(),
                Utils.getModifiedFontData(title.getFont().getFontData(), 18));
        title.setFont(titleFont);

        getShell().addDisposeListener(this);

        // TODO : I18N
        title.setText("Sign in");
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(title);

        addEmail();
        addPassword();
    }

    private void addPassword() {
        Text text = new Text(this, SWT.PASSWORD | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1)
                .align(SWT.FILL, SWT.TOP).applyTo(text);

    }

    private void addEmail() {
        // TODO Auto-generated method stub

    }

}
