package com.vaadin.integration.eclipse.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.vaadin.integration.eclipse.VaadinPlugin;

/**
 * The Eclipse preferences page for Vaadin plugin.
 */

public class VaadinPreferences extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public VaadinPreferences() {
        super(GRID);
        setPreferenceStore(VaadinPlugin.getInstance().getPreferenceStore());
    }

    @Override
    public void createFieldEditors() {
        addField(new BooleanFieldEditor(
                PreferenceConstants.UPDATE_NOTIFICATIONS_IN_NEW_PROJECTS,
                "Enable Vaadin version update notifications in new projects",
                getFieldEditorParent()));
        addField(new BooleanFieldEditor(
                PreferenceConstants.DISABLE_ALL_UPDATE_NOTIFICATIONS,
                "Never inform of Vaadin version updates",
                getFieldEditorParent()));
        new Label(getFieldEditorParent(), SWT.LEFT)
                .setText("If checked, notifications about new versions of Vaadin are not shown "
                        + "even for projects that have these notifications enabled.");
    }

    public void init(IWorkbench workbench) {
    }
}