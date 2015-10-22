package com.vaadin.integration.eclipse.notifications.model;

import java.io.IOException;
import java.io.InputStreamReader;
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

public final class NotificationsProvider {

    private static final String NOTIFICATIONS = "notifications";

    private static final String LINK_URL = "linkUrl";

    private static final String TITLE = "title";

    private static final String ALL_NOTIFICATIONS_URL = "https://vaadin.com/delegate/notifications/personal";

    private static final NotificationsProvider INSTANCE = new NotificationsProvider();

    private static final String UTF8 = "UTF-8";

    private static final String BODY = "body";

    private static final Logger LOG = Logger
            .getLogger(NotificationsProvider.class.getName());

    private NotificationsProvider() {
    }

    public Collection<Notification> getAllNotifications() {
        return getNotifications(ALL_NOTIFICATIONS_URL);
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

    private Notification buildNotification(JSONObject info) {
        Notification notification = new Notification(info.get(TITLE).toString(),
                new Date(), info.get(BODY).toString(), LINK_URL, false);
        return notification;
    }

    private void handleException(Level level, Exception exception) {
        LOG.log(level, null, exception);
    }

    public static NotificationsProvider getInstance() {
        return INSTANCE;
    }

}
