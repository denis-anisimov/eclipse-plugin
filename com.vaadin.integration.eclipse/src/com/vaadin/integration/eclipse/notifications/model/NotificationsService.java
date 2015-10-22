package com.vaadin.integration.eclipse.notifications.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Provides all backend services for notifications.
 *
 */
public final class NotificationsService {

    private static final String ICON_URL = "iconUrl";

    private static final String LINK_TEXT = "linkText";

    private static final String IMAGE_URL = "imageUrl";

    private static final String VALID_FROM = "validFrom";

    private static final String READ = "read";

    private static final String CATEGORY = "category";

    private static final String ID = "id";

    private static final String NOTIFICATIONS = "notifications";

    private static final String LINK_URL = "linkUrl";

    private static final String TITLE = "title";

    private static final String BODY = "body";

    private static final String ALL_NOTIFICATIONS_URL = "https://vaadin.com/delegate/notifications/personal";

    private static final NotificationsService INSTANCE = new NotificationsService();

    private static final String UTF8 = "UTF-8";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssX");

    private static final Logger LOG = Logger
            .getLogger(NotificationsService.class.getName());

    private NotificationsService() {
    }

    public Collection<Notification> getAllNotifications() {
        return getNotifications(ALL_NOTIFICATIONS_URL, true, false);
    }

    public void downloadImages(Collection<Notification> notifications) {

    }

    private Collection<Notification> getNotifications(String url,
            boolean downloadIcons, boolean downloadImagess) {
        Collection<Notification> notifications = getNotifications(url);
        if (downloadIcons) {
            downloadIcons(notifications);
        }
        if (downloadImagess) {
            downloadImages(notifications);
        }
        return notifications;
    }

    private Collection<Notification> getNotifications(String url) {
        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpClient client = builder.build();

        HttpGet request = new HttpGet(url);
        InputStreamReader reader = null;
        try {
            HttpResponse response = client.execute(request);
            JSONParser parser = new JSONParser();

            reader = new InputStreamReader(response.getEntity().getContent(),
                    UTF8);
            JSONObject object = (JSONObject) parser.parse(reader);

            JSONArray array = (JSONArray) object.get(NOTIFICATIONS);
            List<Notification> list = new ArrayList<Notification>(array.size());
            for (int i = 0; i < array.size(); i++) {
                list.add(buildNotification((JSONObject) array.get(i)));
            }

            return list;
        } catch (ClientProtocolException e) {
            handleException(Level.WARNING, e);
        } catch (IOException e) {
            handleException(Level.WARNING, e);
        } catch (IllegalStateException e) {
            handleException(Level.WARNING, e);
        } catch (ParseException e) {
            handleException(Level.WARNING, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    handleException(Level.INFO, e);
                }
            }
        }
        return Collections.emptyList();
    }

    private void downloadIcons(Collection<Notification> notifications) {
        // TODO Auto-generated method stub

    }

    private Notification buildNotification(JSONObject info) {
        Notification.Builder builder = new Notification.Builder();
        builder.setId(getString(info, ID));
        builder.setCategory(getString(info, CATEGORY));
        builder.setTitle(getString(info, TITLE));
        builder.setDescription(getString(info, BODY));
        builder.setLink(getString(info, LINK_URL));
        builder.setTitle(getString(info, TITLE));
        builder.setRead(getBoolean(info, READ));

        builder.setDate(getDate(info, VALID_FROM));
        builder.setImageUrl(getString(info, IMAGE_URL));
        builder.setLinkText(getString(info, LINK_TEXT));
        builder.setIcon(getString(info, ICON_URL));
        return builder.build();
    }

    private String getString(JSONObject object, String property) {
        Object value = object.get(property);
        return value == null ? null : value.toString();
    }

    private boolean getBoolean(JSONObject object, String property) {
        return Boolean.TRUE.toString().equals(getString(object, property));
    }

    private Date getDate(JSONObject object, String property) {
        String value = getString(object, property);
        try {
            return value == null ? null : DATE_FORMAT.parse(value);
        } catch (java.text.ParseException e) {
            handleException(Level.WARNING, e);
            return null;
        }
    }

    private void handleException(Level level, Exception exception) {
        LOG.log(level, null, exception);
    }

    public static NotificationsService getInstance() {
        return INSTANCE;
    }

}
