package com.vaadin.integration.eclipse.background;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.ui.PlatformUI;

import com.vaadin.integration.eclipse.VaadinFacetUtils;
import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.preferences.PreferenceConstants;
import com.vaadin.integration.eclipse.util.ErrorUtil;
import com.vaadin.integration.eclipse.util.PreferenceUtil;
import com.vaadin.integration.eclipse.util.ProjectUtil;
import com.vaadin.integration.eclipse.util.VersionUtil;
import com.vaadin.integration.eclipse.util.data.DownloadableVaadinVersion;
import com.vaadin.integration.eclipse.util.data.MavenVaadinVersion;
import com.vaadin.integration.eclipse.util.network.DownloadManager;
import com.vaadin.integration.eclipse.util.network.MavenVersionManager;

/**
 * User-visible job that checks for new nightly builds and then re-schedules a
 * new check.
 */
public final class NightlyCheckJob extends Job {
    public NightlyCheckJob(String name) {
        super(name);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Checking for new Vaadin nightly builds", 4);
        try {

            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            // map from project with "use latest nightly" to the current
            // Vaadin version number string in the project
            Map<IProject, String> nightlyProjects = getProjectsUsingLatestNightly();
            List<IProject> vaadin7Projects = getVaadin7Projects();

            monitor.worked(1);

            if (nightlyProjects.isEmpty() && vaadin7Projects.isEmpty()) {
                return Status.OK_STATUS;
            } else if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            // update version list
            List<DownloadableVaadinVersion> availableNightlies = DownloadManager
                    .getAvailableNightlyVersions();

            monitor.worked(1);

            final Map<IProject, DownloadableVaadinVersion> possibleUpgrades = new HashMap<IProject, DownloadableVaadinVersion>();

            for (IProject project : nightlyProjects.keySet()) {
                String currentVersion = nightlyProjects.get(project);
                DownloadableVaadinVersion latestNightly = getNightlyToUpgradeTo(
                        currentVersion, availableNightlies);

                if (null != latestNightly
                        && !latestNightly.getVersionNumber().equals(
                                currentVersion)) {
                    possibleUpgrades.put(project, latestNightly);
                }
            }

            final Map<IProject, List<MavenVaadinVersion>> vaadin7Upgrades;
            if (!VaadinPlugin
                    .getInstance()
                    .getPreferenceStore()
                    .getBoolean(
                            PreferenceConstants.DISABLE_ALL_UPDATE_NOTIFICATIONS)) {
                // For each project that has upgrade notifications
                // allowed, find suggestions for Vaadin version upgrades: newest
                // maintenance release with same minor version, newest stable
                // version (any minor) and, if the project uses alpha/beta/rc
                // version, also the newest prerelease version.
                vaadin7Upgrades = getVaadinUpgrades(vaadin7Projects);
            } else {
                // Vaadin 7 upgrade notifications disallowed for all projects
                // regardless of project settings.
                vaadin7Upgrades = new HashMap<IProject, List<MavenVaadinVersion>>();
            }

            monitor.worked(1);

            if (possibleUpgrades.isEmpty() && vaadin7Upgrades.isEmpty()) {
                return Status.OK_STATUS;
            } else if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            // create new task to upgrade Vaadin nightly builds in projects
            NightlyUpgradeJob upgradeJob = new NightlyUpgradeJob(
                    "Upgrade Vaadin nightly builds", possibleUpgrades);
            upgradeJob.setUser(false);
            // avoid concurrent checks and upgrades, "lock" the workspace
            upgradeJob.setRule(MultiRule.combine(
                    NightlyBuildUpdater.RULE_NIGHTLY_UPGRADE, ResourcesPlugin
                            .getWorkspace().getRoot()));
            upgradeJob.schedule();

            monitor.worked(1);

            // "tray notification": the following projects were upgraded to
            // the latest Vaadin nightly builds
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    final UpgradeNotificationPopup popup = new UpgradeNotificationPopup(
                            PlatformUI.getWorkbench().getDisplay(),
                            possibleUpgrades, vaadin7Upgrades);
                    popup.open();
                }
            });

            return Status.OK_STATUS;
        } catch (CoreException e) {
            ErrorUtil.handleBackgroundException(
                    "Failed to update Vaadin nightly build list", e);
            return new Status(IStatus.WARNING, VaadinPlugin.PLUGIN_ID, 1,
                    "Failed to update Vaadin nightly build list", e);
        } finally {
            monitor.done();
        }
    }

    /**
     * Returns the open projects in the workspace for which the
     * "Use latest nightly" option is selected.
     * 
     * @return
     */
    private Map<IProject, String> getProjectsUsingLatestNightly() {
        Map<IProject, String> projectsWithNightly = new HashMap<IProject, String>();
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] projects = workspaceRoot.getProjects();
        for (IProject project : projects) {
            try {
                // add if "use latest nightly" is set
                PreferenceUtil preferences = PreferenceUtil.get(project);
                if (preferences.isUsingLatestNightly()) {
                    String versionNumber = ProjectUtil.getVaadinLibraryVersion(
                            project, true);
                    if (null != versionNumber) {
                        projectsWithNightly.put(project, versionNumber);
                    }
                }
            } catch (CoreException e) {
                ErrorUtil.handleBackgroundException(
                        IStatus.WARNING,
                        "Could not check Vaadin version in project "
                                + project.getName(), e);
            }
        }
        return projectsWithNightly;
    }

    private List<IProject> getVaadin7Projects() {
        List<IProject> vaadin7Projects = new ArrayList<IProject>();
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] projects = workspaceRoot.getProjects();
        for (IProject project : projects) {
            PreferenceUtil preferences = PreferenceUtil.get(project);
            if (VaadinFacetUtils.isVaadinProject(project)
                    && ProjectUtil.isVaadin7(project)) {
                vaadin7Projects.add(project);
            }
        }
        return vaadin7Projects;
    }

    private Map<IProject, List<MavenVaadinVersion>> getVaadinUpgrades(
            List<IProject> vaadinProjects) {
        Map<IProject, List<MavenVaadinVersion>> availableUpgrades = new HashMap<IProject, List<MavenVaadinVersion>>();
        List<MavenVaadinVersion> availableVersions = new ArrayList<MavenVaadinVersion>();
        try {
            availableVersions = MavenVersionManager.getAvailableVersions(true);
        } catch (CoreException e) {
            // Could not load the list of upgrades, handle as if none were
            // available.
            return availableUpgrades;
        }
        for (IProject project : vaadinProjects) {
            try {
                if (!PreferenceUtil.get(project).isUpdateNotificationEnabled()) {
                    continue;
                }
                String currentVersion = ProjectUtil.getVaadinLibraryVersion(
                        project, true);
                List<MavenVaadinVersion> allUpgrades = new ArrayList<MavenVaadinVersion>();
                MavenVaadinVersion newestUpgradeSameMinor = getLatestUpgrade(
                        currentVersion, availableVersions, true, false);
                if (newestUpgradeSameMinor != null) {
                    allUpgrades.add(newestUpgradeSameMinor);
                }
                MavenVaadinVersion newestStableUpgrade = getLatestUpgrade(
                        currentVersion, availableVersions, false, true);
                if (newestStableUpgrade != null
                        && !allUpgrades.contains(newestStableUpgrade)) {
                    allUpgrades.add(newestStableUpgrade);
                }
                // Suggest upgrading to the newest alpha/beta/rc version if
                // the current version is not a stable version
                if (!VersionUtil.isStableVersion(currentVersion)) {
                    MavenVaadinVersion newestUpgrade = getLatestUpgrade(
                            currentVersion, availableVersions, false, false);
                    if (newestUpgrade != null
                            && !allUpgrades.contains(newestUpgrade)) {
                        allUpgrades.add(newestUpgrade);
                    }
                }
                if (!allUpgrades.isEmpty()) {
                    availableUpgrades.put(project, allUpgrades);
                }
            } catch (CoreException e) {
                ErrorUtil.handleBackgroundException(
                        IStatus.WARNING,
                        "Could not check Vaadin version in project "
                                + project.getName(), e);
            }
        }
        return availableUpgrades;
    }

    private MavenVaadinVersion getLatestUpgrade(String current,
            List<MavenVaadinVersion> availableVersions,
            boolean allowOnlySameMinor, boolean allowOnlyStableVersions) {
        String latestAsString = current;
        MavenVaadinVersion result = null;
        for (MavenVaadinVersion version : availableVersions) {
            String versionNumber = version.getVersionNumber();
            // Always require at least the major version to be the same as in
            // the current version.
            boolean acceptVersion = !allowOnlySameMinor ? VersionUtil
                    .isSameVersion(versionNumber, current, 1) : VersionUtil
                    .isSameVersion(versionNumber, current, 2);
            if (allowOnlyStableVersions) {
                acceptVersion = acceptVersion
                        && VersionUtil.isStableVersion(versionNumber);
            }
            if (acceptVersion
                    && VersionUtil.compareVersions(versionNumber,
                            latestAsString) > 0) {
                result = version;
                latestAsString = version.getVersionNumber();
            }
        }
        return result;
    }

    /**
     * Returns the latest nightly version for the same branch as the current
     * version.
     * 
     * @param currentVersion
     * @param availableNightlies
     * @return latest nightly for the branch or null if not found
     */
    public static DownloadableVaadinVersion getNightlyToUpgradeTo(
            String currentVersion,
            List<DownloadableVaadinVersion> availableNightlies) {
        String branch = parseBranch(currentVersion);
        if (null == branch) {
            return null;
        }
        for (DownloadableVaadinVersion version : availableNightlies) {
            // sorted with latest first, return first match
            if (branch.equals(parseBranch(version.getVersionNumber()))) {
                return version;
            }
        }
        return null;
    }

    private static String parseBranch(String versionNumber) {
        return versionNumber.substring(0,
                versionNumber.indexOf(".", versionNumber.indexOf(".") + 1));
    }

}