package com.vaadin.integration.eclipse.notifications.model;

import java.util.Date;

import org.eclipse.swt.graphics.Image;

/**
 * Data model for notification info
 *
 */
public class Notification {

    private final String title;
    private final Date date;

    private final String description;
    private final String link;

    private boolean read;

    protected Notification(String title, Date date, String description,
            String link, boolean read) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.link = link;
        this.read = read;
        // this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public boolean isRead() {
        return read;
    }

    // public NotificationType getType() {
    // return type;
    // }

    public Image getIcon() {
        // TODO:
        return null;
    }

    public Image getHeaderImage() {
        // TODO
        return null;
    }

    public void setRed() {
        read = true;
    }

}
