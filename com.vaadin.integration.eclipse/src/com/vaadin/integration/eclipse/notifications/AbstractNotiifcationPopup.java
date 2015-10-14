package com.vaadin.integration.eclipse.notifications;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.commons.workbench.AbstractWorkbenchNotificationPopup;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;

public class AbstractNotiifcationPopup
        extends AbstractWorkbenchNotificationPopup {

    private static final int MAX_LABEL_CHAR_LENGTH = 120;
    private static final int MAX_DESCRIPTION_CHAR_LENGTH = 500;
    private static final int MIN_HEIGHT = 100;
    private static final int MAX_WIDTH = 400;
    private static final int PADDING_EDGE = 5;

    private ShellActivationListener blockPopupListener;

    protected AbstractNotiifcationPopup(Display display) {
        super(display);
    }

    protected AbstractNotiifcationPopup(Display display, int style) {
        super(display, style);
    }

    @Override
    public void closeFade() {
        // the close job can not be extended to handle the blocked popup.
        // therefore the closing fade is checked and the job is rescheduled.
        if (blockPopupListener.isPopupBlocked()) {
            scheduleAutoClose();
            return;
        }
        if (blockPopupListener.isPopupReactivated()) {
            // the popup has been reactivated but a close job will still be
            // active and may be triggered short afterwards. For this reason
            // schedule another time interval
            blockPopupListener.reset();
            scheduleAutoClose();
            return;
        }
        super.closeFade();
    }

    @Override
    public boolean close() {
        if (blockPopupListener != null) {
            PlatformUI.getWorkbench().getDisplay().removeFilter(SWT.Activate,
                    blockPopupListener);
        } else {
            // TODO
            // log(ERROR_LISTENER_NULL);
        }
        return super.close();
    }

    @Override
    public void create() {
        super.create();
        registerModalShellListener();
        Label titleLabel = getTitleLabel(getContents());
        if (titleLabel != null) {
            titleLabel.setCursor(getParentShell().getDisplay()
                    .getSystemCursor(SWT.CURSOR_ARROW));
        }
    }

    @Override
    protected String getPopupShellTitle() {
        // TODO
        return "Vaadin Notification";
    }

    @Override
    protected Color getTitleForeground() {
        return CommonFormUtil.getSharedColors().getColor(IFormColors.TITLE);
    }

    @Override
    protected Image getPopupShellImage(int maximumHeight) {
        // TODO
        return super.getPopupShellImage(maximumHeight);
    }

    @Override
    protected void createContentArea(Composite parent) {
        Composite contentComposite = new Composite(parent, SWT.NO_FOCUS);
        GridLayout gridLayout = new GridLayout(2, false);
        GridDataFactory.fillDefaults().grab(true, false)
                .align(SWT.FILL, SWT.TOP).applyTo(contentComposite);
        contentComposite.setLayout(gridLayout);

        // icon label:
        new Label(contentComposite, SWT.NO_FOCUS);

        final Label labelText = new Label(contentComposite,
                SWT.WRAP | SWT.NO_FOCUS);
        labelText.setForeground(CommonColors.TEXT_QUOTED);
    }

    @Override
    protected void initializeBounds() {
        Rectangle screenArea = getPrimaryClientArea();
        Shell shell = getShell();
        // superclass computes size with SWT.DEFAULT,SWT.DEFAULT. For long text
        // this causes a large width
        // and a small height. Afterwards the height gets maxed to the
        // MIN_HEIGHT value and the width gets trimmed
        // which results in text floating out of the window
        Point initialSize = shell.computeSize(MAX_WIDTH, SWT.DEFAULT);
        int height = Math.max(initialSize.y, MIN_HEIGHT);
        int width = Math.min(initialSize.x, MAX_WIDTH);

        Point size = new Point(width, height);
        shell.setLocation(
                screenArea.width + screenArea.x - size.x - PADDING_EDGE,
                screenArea.height + screenArea.y - size.y - PADDING_EDGE);
        shell.setSize(size);
    }

    private Rectangle getPrimaryClientArea() {
        Monitor primaryMonitor = getShell().getDisplay().getPrimaryMonitor();
        return primaryMonitor != null ? primaryMonitor.getClientArea()
                : getShell().getDisplay().getClientArea();
    }

    private void registerModalShellListener() {
        blockPopupListener = new ShellActivationListener();
        PlatformUI.getWorkbench().getDisplay().addFilter(SWT.Activate,
                blockPopupListener);
    }

    private Label getTitleLabel(Control widget) {
        if (widget instanceof Label) {
            Label label = (Label) widget;
            if (getPopupShellTitle().equals(label.getText())) {
                return label;
            }
        }
        if (widget instanceof Composite) {
            for (Control control : ((Composite) widget).getChildren()) {
                Label label = getTitleLabel(control);
                if (label != null) {
                    return label;
                }
            }
        }
        return null;
    }

    private final class ShellActivationListener implements Listener {

        private boolean isPopupBlocked;
        private boolean isPopupReactivated;
        private String title;
        private final Label titleLabel;

        ShellActivationListener() {
            title = getPopupShellTitle();
            titleLabel = getTitleLabel(getContents());
            if (titleLabel != null) {
                title = titleLabel.getText();
            }
            // check existing shells
            IWorkbench workbench = PlatformUI.getWorkbench();
            for (Shell shell : workbench.getDisplay().getShells()) {
                if (isVisibleAndModal(shell) && !isPopupBlocked) {
                    deactivate();
                    break;
                }
            }
        }

        public void handleEvent(Event event) {
            if (!isPopupOpen()) {
                return;
            }
            Widget w = event.widget;
            if (w instanceof Shell) {
                Shell shell = (Shell) w;
                if (isVisibleAndModal(shell)) {
                    if (!isPopupBlocked()) {
                        deactivate();
                    }
                } else if (isPopupBlocked()) {
                    isPopupReactivated = true;
                    activate();
                }
            }
        }

        private boolean isPopupBlocked() {
            return isPopupBlocked;
        }

        private boolean isPopupReactivated() {
            return isPopupReactivated;
        }

        private void reset() {
            isPopupReactivated = false;
        }

        private boolean isPopupOpen() {
            return getShell() != null && !getShell().isDisposed();
        }

        private boolean isVisibleAndModal(Shell shell) {
            int modal = SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL
                    | SWT.PRIMARY_MODAL;
            return shell.isVisible() && (shell.getStyle() & modal) != 0;
        }

        private void deactivate() {
            isPopupBlocked = true;
            if (titleLabel != null) {
                // TODO
                titleLabel.setText("(Waiting for focus) " + title);
            }
        }

        private void activate() {
            isPopupBlocked = false;
            if (titleLabel != null) {
                titleLabel.setText(title);
            }
        }
    }
}
