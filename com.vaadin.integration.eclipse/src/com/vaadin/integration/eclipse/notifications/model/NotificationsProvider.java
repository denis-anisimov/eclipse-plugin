package com.vaadin.integration.eclipse.notifications.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    private NotificationsProvider() {
    }

    public Collection<Notification> getAllNotifications() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpClient client = builder.build();

        HttpGet request = new HttpGet(ALL_NOTIFICATIONS_URL);
        try {
            HttpResponse response = client.execute(request);
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(new InputStreamReader(
                    response.getEntity().getContent(), UTF8));

            JSONArray array = (JSONArray) object.get(NOTIFICATIONS);
            List<Notification> list = new ArrayList<Notification>(array.size());
            for (int i = 0; i < array.size(); i++) {
                JSONObject info = (JSONObject) array.get(i);

                Notification notification = new Notification(
                        info.get(TITLE).toString(), new Date(),
                        info.get(BODY).toString(), LINK_URL, false);
                list.add(notification);
            }

            return list;
        } catch (ClientProtocolException e) {
            // TODO
        } catch (IOException e) {
            // TODO
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static NotificationsProvider getInstance() {
        return INSTANCE;
    }
}
