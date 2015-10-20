package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Shows new notifications informational popup (without exact notification
 * content).
 * 
 * @author denis
 *
 */
class NewNotificationsPopup extends AbstractNotificationPopup {

    NewNotificationsPopup(Control control) {
        super(control);
    }

    @Override
    protected void createContentArea(Composite parent) {
        super.createContentArea(parent);

        FewNotificationsComposite control = new FewNotificationsComposite(
                parent, getManager());
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1)
                .align(SWT.FILL, SWT.FILL).applyTo(control);
    }
}
