package com.vaadin.integration.eclipse.background;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.vaadin.integration.eclipse.util.ProjectUtil;
import com.vaadin.integration.eclipse.util.data.DownloadableVaadinVersion;
import com.vaadin.integration.eclipse.util.data.MavenVaadinVersion;

public class UpgradeNotificationPopup extends AbstractNotificationPopup {

    private final Map<IProject, DownloadableVaadinVersion> upgrades;
    private final Map<IProject, List<MavenVaadinVersion>> nonNightlyUpgrades;

    private MouseAdapter closeListener = new MouseAdapter() {
        @Override
        public void mouseUp(MouseEvent e) {
            close();
        }
    };

    public UpgradeNotificationPopup(Display display,
            Map<IProject, DownloadableVaadinVersion> upgrades,
            Map<IProject, List<MavenVaadinVersion>> nonNightlyUpgrades) {
        super(display);
        this.upgrades = upgrades;
        this.nonNightlyUpgrades = nonNightlyUpgrades;

        // keep open indefinitely (until user clicks)
        setDelayClose(-1);
    }

    @Override
    protected void createTitleArea(Composite parent) {
        ((GridData) parent.getLayoutData()).heightHint = 24;

        Label titleCircleLabel = new Label(parent, SWT.NONE);
        titleCircleLabel.setText("Vaadin version upgrades");
        titleCircleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                true));
        titleCircleLabel.setCursor(parent.getDisplay().getSystemCursor(
                SWT.CURSOR_HAND));

        titleCircleLabel.addMouseListener(closeListener);
        parent.addMouseListener(closeListener);
    }

    @Override
    protected void createContentArea(Composite parent) {
        if (!upgrades.isEmpty()) {
            Label label = new Label(parent, SWT.None);
            label.setText("The following nightly versions will be upgraded");
            label.setBackground(parent.getBackground());
            label.addMouseListener(closeListener);
            for (IProject project : upgrades.keySet()) {
                Label l = new Label(parent, SWT.None);
                l.setText("Project " + project.getName() + ": "
                        + upgrades.get(project));
                l.setBackground(parent.getBackground());
                l.addMouseListener(closeListener);
            }
            // Add some space before the possible upgradable Vaadin 7 projects.
            label = new Label(parent, SWT.None);
            label.setBackground(parent.getBackground());
            label.addMouseListener(closeListener);
        }

        if (!nonNightlyUpgrades.isEmpty()) {
            Label label = new Label(parent, SWT.None);
            label.setText("New Vaadin versions are available for the following projects");
            label.setBackground(parent.getBackground());
            label.addMouseListener(closeListener);
            for (IProject project : nonNightlyUpgrades.keySet()) {
                String currentVersion = null;
                try {
                    currentVersion = ProjectUtil.getVaadinLibraryVersion(
                            project, true);
                } catch (CoreException e) {
                }
                Label l = new Label(parent, SWT.None);
                String projectString = project.getName();
                if (currentVersion != null) {
                    projectString += "(" + currentVersion + ")";
                }
                List<MavenVaadinVersion> upgrades = nonNightlyUpgrades
                        .get(project);
                l.setText("Project " + projectString + ": "
                        + upgrades.toString().replaceAll("^\\[|\\]$", ""));
                l.setBackground(parent.getBackground());
                l.addMouseListener(closeListener);
            }
        }
        parent.addMouseListener(closeListener);
    }

    @Override
    protected String getPopupShellTitle() {
        return "Vaadin version upgrades";
    }
}
