package model;

public class Event {

    private int eventId;
    private String title;
    private String description;
    private String date;
    private String time;
    private String venue;
    private String category;
    private String organizerName;

    public Event(int eventId, String title, String description,
                 String date, String time, String venue,
                 String category, String organizerName) {

        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.category = category;
        this.organizerName = organizerName;
    }

    public int getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getVenue() {
        return venue;
    }

    public String getCategory() {
        return category;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void displayEvent() {
        System.out.println("Event ID: " + eventId);
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);
        System.out.println("Venue: " + venue);
        System.out.println("Category: " + category);
        System.out.println("Organizer: " + organizerName);
    }
}