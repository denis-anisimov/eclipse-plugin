package com.vaadin.integration.eclipse.notifications;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = Messages.class.getPackage()
            .getName() + ".messages"; //$NON-NLS-1$

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String Notifications_BackAction;
    public static String Notifications_ClearAll;
    public static String Notifications_NotificationInfoReadMore;
    public static String Notifications_PopupNotificationsTitle;
    public static String Notifications_PopupNotificationTitle;
    public static String Notifications_ReadMore;
    public static String Notifications_Settings;
    public static String Notifications_SeveralNotificaitonsMsgParameter;
    public static String Notifications_SeveralNotificationsMessage;
    public static String Notifications_SignIn;
    public static String Notifications_SignInEmail;
    public static String Notifications_SignInError;
    public static String Notifications_SignInItemSeeYourNotifications;
    public static String Notifications_SignInItemUseAccount;
    public static String Notifications_SignInPassword;
    public static String Notifications_SignInWithSuffix;
    public static String Notifications_SignOut;
    public static String Notifications_TokenErrorMsg;
    public static String Notifications_TokenViewTitle;
    public static String Notifications_waitingFocus;
}
