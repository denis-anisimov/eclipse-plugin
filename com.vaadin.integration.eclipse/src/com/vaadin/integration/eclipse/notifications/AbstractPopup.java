package com.vaadin.integration.eclipse.notifications;

import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.vaadin.integration.eclipse.VaadinPlugin;

abstract class AbstractPopup extends AbstractNotificationPopup {

    private Font boldPopupFont;
    private Font regularFont;

    protected AbstractPopup(Display display) {
        super(display);
    }

    protected AbstractPopup(Display display, int style) {
        super(display, style);
    }

    @Override
    public void create() {
        boldPopupFont = Utils.createFont(13, SWT.BOLD, Utils.HELVETICA,
                Utils.ARIAL);
        regularFont = Utils.createFont(13, SWT.NORMAL, Utils.HELVETICA,
                Utils.ARIAL);
        super.create();
        getShell().addDisposeListener(new CleanupListener());
    }

    @Override
    protected Control createContents(Composite parent) {
        Control content = super.createContents(parent);
        // reset gradient background image
        ((Composite) content).setBackgroundMode(SWT.INHERIT_NONE);
        return content;
    }

    @Override
    protected String getPopupShellTitle() {
        return Messages.Notifications_PopupNotificationsTitle;
    }

    @Override
    protected Image getPopupShellImage(int maximumHeight) {
        return VaadinPlugin.getInstance().getImageRegistry()
                .get(Utils.REGULAR_NOTIFICATION_ICON);
    }

    @Override
    protected void createContentArea(Composite parent) {
        adjustMargins(parent);
    }

    protected void adjustMargins(Composite parent) {
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
    }

    protected void adjustHeader(Composite parent) {
        GridLayout layout = (GridLayout) parent.getLayout();

        layout.marginWidth = 3;
        layout.marginRight = 5;
        layout.marginLeft = 5;
        // layout.horizontalSpacing = 3;
        cancelVerticalSpace(layout);
    }

    protected void cancelVerticalSpace(GridLayout layout) {
        layout.verticalSpacing = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.marginHeight = 0;
    }

    protected Font getBoldFont() {
        return boldPopupFont;
    }

    protected Font getRegularFont() {
        return regularFont;
    }

    private class CleanupListener implements DisposeListener {
        public void widgetDisposed(DisposeEvent e) {
            if (boldPopupFont != null) {
                boldPopupFont.dispose();
                regularFont.dispose();
            }
        }
    }
}
