package model;

public class Registration {

    private int registrationId;
    private int studentId;
    private int eventId;
    private String registrationDate;

    public Registration(int registrationId, int studentId,
                        int eventId, String registrationDate) {

        this.registrationId = registrationId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void displayRegistration() {
        System.out.println("Registration ID: " + registrationId);
        System.out.println("Student ID: " + studentId);
        System.out.println("Event ID: " + eventId);
        System.out.println("Registration Date: " + registrationDate);
    }
}