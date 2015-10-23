package com.vaadin.integration.eclipse.notifications.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.MessageFormat;
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
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.vaadin.integration.eclipse.VaadinPlugin;
import com.vaadin.integration.eclipse.notifications.Utils;

/**
 * Provides all backend services for notifications.
 *
 */
public final class NotificationsService {

    private static final String IMAGES_CACHE = "notification-images-cache";

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

    private static final int ICON_SIZE = 40;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssX");

    private final Object lock;

    private static final Logger LOG = Logger
            .getLogger(NotificationsService.class.getName());

    private NotificationsService() {
        lock = new Object();
    }

    public Collection<Notification> getAllNotifications() {
        return getNotifications(ALL_NOTIFICATIONS_URL, true, false);
    }

    public void downloadImages(Collection<Notification> notifications) {
        HttpClient client = createHttpClient();
        downloadImages(client, notifications);
        HttpClientUtils.closeQuietly(client);
    }

    private void downloadImages(HttpClient client,
            Collection<Notification> notifications) {
        File folder = getCacheFolder();
        synchronized (lock) {
            for (Notification notification : notifications) {
                ensureImageRegistered(client, folder,
                        notification.getImageUrl(), false);
            }
        }
    }

    private Collection<Notification> getNotifications(String url,
            boolean downloadIcons, boolean downloadImagess) {
        HttpClient client = createHttpClient();
        Collection<Notification> notifications = getNotifications(client, url);
        if (downloadIcons) {
            downloadIcons(client, notifications);
        }
        if (downloadImagess) {
            downloadImages(client, notifications);
        }
        return notifications;
    }

    private Collection<Notification> getNotifications(HttpClient client,
            String url) {
        HttpGet request = new HttpGet(url);
        InputStreamReader reader = null;
        try {
            HttpResponse response = client.execute(request);
            JSONParser parser = new JSONParser();

            reader = new InputStreamReader(response.getEntity().getContent(),
                    UTF8);
            JSONObject object = (JSONObject) parser.parse(reader);

            HttpClientUtils.closeQuietly(response);
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
        HttpClientUtils.closeQuietly(client);
        return Collections.emptyList();
    }

    private HttpClient createHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        return builder.build();
    }

    private void downloadIcons(HttpClient client,
            Collection<Notification> notifications) {
        File folder = getCacheFolder();
        synchronized (lock) {
            for (Notification notification : notifications) {
                ensureImageRegistered(client, folder, notification.getIconUrl(),
                        true);
            }
        }
    }

    private void ensureImageRegistered(HttpClient client, File folder,
            String url, boolean icon) {
        Image image = VaadinPlugin.getInstance().getImageRegistry().get(url);
        if (image == null) {
            registerImage(client, folder, url, icon);
        }
    }

    private void registerImage(HttpClient client, File folder, String url,
            boolean icon) {
        try {
            ImageData data = getImage(client, url, folder, icon);
            if (data != null) {
                VaadinPlugin.getInstance().getImageRegistry().put(url,
                        ImageDescriptor.createFromImageData(data));
            }
        } catch (SWTException e) {
            handleException(Level.WARNING, e);
        }
    }

    /**
     * Image infrastructure has bad design: it doesn't declare thrown exception
     * in method signatures but it throws unchecked exceptions. So SWTException
     * is explicitly declared to be able to catch it.
     */
    private ImageData getImage(HttpClient client, String url, File cacheFolder,
            boolean icon) throws SWTException {
        String id = getUniqueId(url);
        if (cacheFolder == null || id == null) {
            return downloadImage(client, url, icon);
        }
        File file = new File(cacheFolder, id);
        try {
            if (file.exists()) {
                try {
                    // this can throw unchecked exception. Consider this as
                    // broken cache file and reset it via downloading.
                    return new ImageData(new FileInputStream(file));
                } catch (SWTException e) {
                    handleException(Level.WARNING, e);
                }
            }
            ImageData data = downloadImage(client, url, icon);
            cacheImage(file, data);
            return data;
        } catch (FileNotFoundException e) {
            handleException(Level.WARNING, e);
        }
        return null;
    }

    private void cacheImage(File file, ImageData data)
            throws FileNotFoundException {
        FileOutputStream stream = new FileOutputStream(file);
        ImageLoader loader = new ImageLoader();
        loader.data = new ImageData[] { data };
        loader.save(stream, data.type);
        try {
            stream.close();
        } catch (IOException e) {
            handleException(Level.INFO, e);
        }
    }

    private String getUniqueId(String urlString) {
        try {
            URL url = new URL(urlString);
            String file = url.getFile();
            if (file.charAt(0) == '/') {
                file = file.substring(1);
            }
            return URLEncoder.encode(file, UTF8);
        } catch (MalformedURLException e) {
            handleException(Level.WARNING, e);
        } catch (UnsupportedEncodingException e) {
            handleException(Level.WARNING, e);
        }

        return null;

    }

    private ImageData downloadImage(HttpClient client, String url,
            boolean icon) {
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        InputStream stream = null;
        try {
            response = client.execute(request);

            stream = response.getEntity().getContent();
            ImageData data = new ImageData(stream);
            Point size;
            if (icon) {
                size = scaleIconSize(new Point(data.width, data.height));
            } else {
                size = scaleImageSize(new Point(data.width, data.height));
            }

            if (size == null) {
                log(Level.INFO, "{0} {1} has zero size and is not downloaded",
                        icon ? "Icon" : "Image", url);
                return null;
            } else {
                log(Level.INFO, "{0} {1} is downloaded",
                        icon ? "Icon" : "Image", url);
                return data.scaledTo(size.x, size.y);
            }
        } catch (ClientProtocolException e) {
            handleException(Level.WARNING, e);
        } catch (IOException e) {
            handleException(Level.WARNING, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    handleException(Level.INFO, e);
                }
            }
            if (response != null) {
                HttpClientUtils.closeQuietly(response);
            }
        }
        return null;
    }

    private void log(Level level, String pattern, Object... params) {
        LOG.log(level, MessageFormat.format(pattern, params));
    }

    private Point scaleIconSize(Point original) {
        int max = Math.max(original.x, original.y);
        if (max == 0) {
            return null;
        }
        return new Point((ICON_SIZE * original.x) / max,
                (ICON_SIZE * original.y) / max);
    }

    private Point scaleImageSize(Point original) {
        return new Point(Utils.MAX_WIDTH,
                (Utils.MAX_WIDTH * original.y) / original.x);
    }

    private File getCacheFolder() {
        IPath location = VaadinPlugin.getInstance().getStateLocation();
        location = location.append(IMAGES_CACHE);
        File file = location.makeAbsolute().toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.exists()) {
            return file;
        } else {
            log(Level.WARNING,
                    "Cache directory {} has not been created. Proceed without cache.",
                    file.getPath());
            return null;
        }
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
