package com.vaadin.integration.eclipse.notifications;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

public final class Utils {

    public static final String SIGN_IN_ICON = "icons.sign-in-icon40";
    public static final int MAX_WIDTH = 350;

    public static final String FORWARD_SUFFIX = " \u00BB";

    static final String HELVETICA = "Helvetica";
    static final String ARIAL = "Arial";

    static final String REGULAR_NOTIFICATION_ICON = "icons.vaadin-icon16";
    static final String NEW_NOTIFICATION_ICON = "icons.vaadin-icon16-new";
    static final String GO_ICON = "icons.triangle-icon";
    static final String RETURN_ICON = "icons.chevron-left-icon";
    static final String CLEAR_ALL_ICON = "icons.bell-slash-icon";
    static final String NEW_ICON = "icons.dot";

    static final String SIGN_IN_BUTTON = "icons.sign-in-btn";
    static final String SIGN_IN_PRESSED_BUTTON = "icons.sign-in-hit-btn";
    static final String SIGN_IN_HOVER_BUTTON = "icons.sign-in-hover-btn";

    static final String SUBMIT_BUTTON = "icons.submit-in-btn";
    static final String SUBMIT_PRESSED_BUTTON = "icons.submit-in-hit-btn";
    static final String SUBMIT_HOVER_BUTTON = "icons.submit-in-hover-btn";

    static final String NEW_NOTIFICATIONS_ICON = "icons.red-bell";

    static final String BROWSER_ID = UUID.randomUUID().toString();

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());
    static final String SIGN_IN_URL = "https://vaadin.com/home?p_p_id=58&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&saveLastPath=false&_58_struts_action=%2Flogin%2Flogin";
    static final String SETTINGS_URL = "https://vaadin.com";
    static final int MIN_HEIGHT = 100;
    static final int PADDING_EDGE = 5;

    static final int ITEM_HEIGHT = 68;

    private Utils() {
    }

    public static FontData[] getModifiedFontData(FontData[] baseData,
            int height) {
        FontData[] styleData = new FontData[baseData.length];
        for (int i = 0; i < styleData.length; i++) {
            FontData base = baseData[i];
            styleData[i] = new FontData(base.getName(), height,
                    base.getStyle());
        }
        return styleData;
    }

    public static FontData[] getModifiedFontData(FontData[] baseData,
            int height, int style) {
        FontData[] styleData = new FontData[baseData.length];
        for (int i = 0; i < styleData.length; i++) {
            FontData base = baseData[i];
            styleData[i] = new FontData(base.getName(), height,
                    base.getStyle() | style);
        }
        return styleData;
    }

    public static Font createFont(int height, int style,
            String... symbolicFontNames) {
        FontRegistry registry = JFaceResources.getFontRegistry();
        FontData[] data = null;
        for (String name : symbolicFontNames) {
            if (registry.hasValueFor(name)) {
                data = registry.getFontData(name);
                break;
            }
        }
        if (data == null) {
            data = registry.defaultFont().getFontData();
        }
        return new Font(PlatformUI.getWorkbench().getDisplay(),
                getModifiedFontData(data, height, style));
    }

    public static IWebBrowser openUrl(String url) throws UrlOpenException {
        boolean urlOpened = true;
        IWebBrowser browser = null;
        try {
            browser = PlatformUI.getWorkbench().getBrowserSupport()
                    .createBrowser(Utils.BROWSER_ID);
            browser.openURL(new URL(url));
        } catch (PartInitException exception) {
            LOG.log(Level.SEVERE, null, exception);
            if (Program.launch(url)) {
                LOG.info("URL is opened via external program");
            } else {
                urlOpened = false;
            }
        } catch (MalformedURLException exception) {
            urlOpened = false;
            LOG.log(Level.SEVERE, null, exception);
        }
        if (urlOpened) {
            return browser;
        } else {
            throw new UrlOpenException();
        }
    }

    public static boolean isControlClicked(Event event, Control control) {
        if (!control.isDisposed() && !event.widget.isDisposed()
                && control.getShell()
                        .equals(event.widget.getDisplay().getActiveShell())) {
            Point location = control.getDisplay().getCursorLocation();
            Point listLocation = control.toDisplay(0, 0);
            Point size = control.getSize();
            Rectangle bounds = new Rectangle(listLocation.x, listLocation.y,
                    size.x, size.y);
            return bounds.contains(location);
        } else {
            return false;
        }
    }

    static class UrlOpenException extends Exception {

        private static final long serialVersionUID = 3105958916452406886L;

    }

}
