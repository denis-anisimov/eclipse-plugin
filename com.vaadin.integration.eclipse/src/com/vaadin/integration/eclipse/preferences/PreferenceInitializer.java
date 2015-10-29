package com.vaadin.integration.eclipse.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.vaadin.integration.eclipse.VaadinPlugin;

/**
 * Initializes the preferences to their default values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = VaadinPlugin.getInstance()
                .getPreferenceStore();
        store.setDefault(
                PreferenceConstants.UPDATE_NOTIFICATIONS_IN_NEW_PROJECTS, true);
        store.setDefault(PreferenceConstants.DISABLE_ALL_UPDATE_NOTIFICATIONS,
                false);
        store.setDefault(PreferenceConstants.NOTIFICATIONS_POLLING_INTERVAL,
                120);

        store.setDefault(PreferenceConstants.NOTIFICATIONS_ENABLED, true);

        store.setDefault(PreferenceConstants.NOTIFICATIONS_POPUP_ENABLED, true);
        store.setDefault(PreferenceConstants.NOTIFICATIONS_USER_TOKEN, "");
        store.setDefault(PreferenceConstants.NOTIFICATIONS_ANONYMOUS_TOKEN, "");
        store.setDefault(PreferenceConstants.NOTIFICATIONS_STAT_ENABLED, true);
        store.setDefault(PreferenceConstants.NOTIFICATIONS_FETCH_ON_START,
                true);
    }
}