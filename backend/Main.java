import model.Student;
import model.Organizer;
import model.Admin;
import model.Event;
import model.Registration;

public class Main {

    public static void main(String[] args) {

        // Create a Student object
        Student student = new Student(
                1,
                "Rahul Kumar",
                "rahul@college.edu",
                "Computer Science"
        );

        // Create an Organizer object
        Organizer organizer = new Organizer(
                2,
                "Priya Sharma",
                "priya@college.edu",
                "Computer Science Club"
        );

        // Create an Admin object
        Admin admin = new Admin(
                3,
                "Admin User",
                "admin@college.edu",
                "Super Admin"
        );

        // Create an Event object
        Event event = new Event(
                101,
                "Tech Fest 2026",
                "Annual college technology festival",
                "2026-10-15",
                "10:00 AM",
                "Main Auditorium",
                "Technical",
                organizer.getName()
        );

        // Create a Registration object
        Registration registration = new Registration(
                1001,
                student.getId(),
                event.getEventId(),
                "2026-09-24"
        );

        // Display information
        System.out.println("===== STUDENT =====");
        student.displayUser();

        System.out.println();

        System.out.println("===== ORGANIZER =====");
        organizer.displayUser();

        System.out.println();

        System.out.println("===== ADMIN =====");
        admin.displayUser();

        System.out.println();

        System.out.println("===== EVENT =====");
        event.displayEvent();

        System.out.println();

        System.out.println("===== REGISTRATION =====");
        registration.displayRegistration();
    }
}