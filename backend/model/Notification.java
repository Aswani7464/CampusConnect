package model;

public class Notification {

    private int notificationId;
    private int studentId;
    private int eventId;
    private String title;
    private String message;
    private String eventDate;
    private String venue;
    private String createdAt;
    private boolean read;

    public Notification(
            int notificationId,
            int studentId,
            int eventId,
            String title,
            String message,
            String eventDate,
            String venue,
            String createdAt) {

        this.notificationId = notificationId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.title = title;
        this.message = message;
        this.eventDate = eventDate;
        this.venue = venue;
        this.createdAt = createdAt;
        this.read = false;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getVenue() {
        return venue;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }
}