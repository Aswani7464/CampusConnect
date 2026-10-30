package model;

public class Registration {

    private int registrationId;
    private int studentId;
    private int eventId;
    private String registrationDate;

    private String fullName;
    private String registerNumber;
    private String emailAddress;
    private String phoneNumber;
    private String department;
    private String yearOfStudy;

    public Registration(int registrationId, int studentId,
                        int eventId, String registrationDate) {

        this(registrationId, studentId, eventId, registrationDate,
                "", "", "", "", "", "");
    }

    public Registration(int registrationId, int studentId,
                        int eventId, String registrationDate,
                        String fullName, String registerNumber,
                        String emailAddress, String phoneNumber,
                        String department, String yearOfStudy) {

        this.registrationId = registrationId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
        this.fullName = fullName;
        this.registerNumber = registerNumber;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
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

    public String getFullName() {
        return fullName;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void displayRegistration() {
        System.out.println("Registration ID: " + registrationId);
        System.out.println("Student ID: " + studentId);
        System.out.println("Event ID: " + eventId);
        System.out.println("Registration Date: " + registrationDate);
        System.out.println("Full Name: " + fullName);
        System.out.println("Register Number: " + registerNumber);
        System.out.println("Email Address: " + emailAddress);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Department: " + department);
        System.out.println("Year of Study: " + yearOfStudy);
    }
}