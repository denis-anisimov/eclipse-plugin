package com.vaadin.integration.eclipse.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.vaadin.integration.eclipse.VaadinPlugin;

/**
 * The Eclipse preferences page for Vaadin plugin.
 */

public class VaadinPreferences extends PreferencePage
        implements IWorkbenchPreferencePage {

    private final List<VaadinFieldEditor> editors;

    public VaadinPreferences() {
        setPreferenceStore(VaadinPlugin.getInstance().getPreferenceStore());
        editors = new ArrayList<VaadinFieldEditor>();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public boolean performOk() {
        for (VaadinFieldEditor editor : editors) {
            editor.store();
            editor.setPresentsDefaultValue(false);
        }
        return true;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setFont(parent.getFont());

        createFieldEditors(composite);

        createNotificationsSecton(composite);

        // checkState();
        return composite;
    }

    private void createFieldEditors(Composite composite) {
        addField(new VaadinBooleanFieldEditor(
                PreferenceConstants.UPDATE_NOTIFICATIONS_IN_NEW_PROJECTS,
                "Enable Vaadin version update notifications in new projects",
                composite));
        addField(new VaadinBooleanFieldEditor(
                PreferenceConstants.DISABLE_ALL_UPDATE_NOTIFICATIONS,
                "Never inform of Vaadin version updates", composite));
        new Label(composite, SWT.LEFT).setText(
                "If checked, notifications about new versions of Vaadin are not shown "
                        + "even for projects that have these notifications enabled.\nIn addition, no usage statistics will be collected. ");
    }

    private <T extends FieldEditor & VaadinFieldEditor> void addField(
            T editor) {
        editors.add(editor);
        editor.setPage(this);
        editor.setPreferenceStore(getPreferenceStore());
        editor.load();
    }

    private void createNotificationsSecton(Composite composite) {
        final ExpandableComposite expandable = new ExpandableComposite(
                composite, SWT.NONE, ExpandableComposite.TWISTIE
                        | ExpandableComposite.CLIENT_INDENT);
        // expandable.setExpanded(true);

        expandable.addExpansionListener(new ExpansionListener());

        Composite panel = new Composite(expandable, SWT.NONE);
        expandable.setClient(panel);
        panel.setLayout(new GridLayout(1, false));
        // TODO : I18N
        expandable.setText("Vaadin notifications");
        expandable.setFont(CommonFonts.BOLD);

        addField(new VaadinBooleanFieldEditor(
                PreferenceConstants.NOTIFICATIONS_ENABLED,
                "Enable Vaadin notifictions", panel));

        addField(new VaadinBooleanFieldEditor(
                PreferenceConstants.NOTIFICATIONS_POPUP_ENABLED,
                "Inform me about new notifications using popup", panel));

        addField(new VaadinBooleanFieldEditor(
                PreferenceConstants.NOTIFICATIONS_STAT_ENABLED,
                "Allow collecting notification statistics", panel));
    }

    private interface VaadinFieldEditor {
        void store();

        void setPresentsDefaultValue(boolean booleanValue);
    }

    private static class VaadinBooleanFieldEditor extends BooleanFieldEditor
            implements VaadinFieldEditor {

        VaadinBooleanFieldEditor(String name, String label,
                Composite composite) {
            super(name, label, composite);
        }

        @Override
        public void setPresentsDefaultValue(boolean booleanValue) {
            super.setPresentsDefaultValue(booleanValue);
        }
    }

    private static final class ExpansionListener extends ExpansionAdapter {
        @Override
        public void expansionStateChanged(ExpansionEvent e) {
            ((Control) e.getSource()).getParent().layout();
        }
    }

}