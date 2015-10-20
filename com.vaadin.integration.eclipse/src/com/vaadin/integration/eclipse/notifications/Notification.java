package com.vaadin.integration.eclipse.notifications;

import java.util.Date;

/**
 * Data model for notification info
 *
 */
class Notification {

    private final String title;
    private final Date date;

    private final String description;
    private final String link;

    private final NotificationType type;

    private boolean read;

    Notification(String title, Date date, String description, String link,
            NotificationType type, boolean read) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.link = link;
        this.read = read;
        this.type = type;
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

    public NotificationType getType() {
        return type;
    }

    void setRed() {
        read = true;
    }

}
