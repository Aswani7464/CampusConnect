import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import model.Attendance;
import model.Event;
import model.Registration;
import model.Notification;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class Server {

    private static final List<Event> events = new ArrayList<>();
    private static final Map<Integer, Integer> eventStudentLimits = new HashMap<>();
    private static final Map<Integer, Integer> eventFees = new HashMap<>();

    private static final List<Registration> registrations = new ArrayList<>();
    private static final List<Attendance> attendanceRecords = new ArrayList<>();

    // NOTIFICATION STORAGE
    private static final List<Notification> notifications = new ArrayList<>();

    private static final Map<String, StudentAccount> studentAccounts =
            new HashMap<>();

    private static final Map<String, OrganizerAccount> organizerAccounts =
            new HashMap<>();

    private static int nextStudentId = 2;
    private static int nextOrganizerId = 2;
    private static int nextEventId = 104;
    private static int nextRegistrationId = 1001;
    private static int nextAttendanceId = 1;
    private static int nextNotificationId = 1;

    private static final Path ACCOUNTS_FILE =
            Path.of("campusconnect-accounts.properties");

    private static class StudentAccount {

        private final int studentId;
        private final String name;
        private final String email;
        private final String password;

        StudentAccount(int studentId, String name,
                       String email, String password) {

            this.studentId = studentId;
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }

    private static class OrganizerAccount {

        private final int organizerId;
        private final String name;
        private final String email;
        private final String password;

        OrganizerAccount(int organizerId, String name,
                         String email, String password) {

            this.organizerId = organizerId;
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }

    public static void main(String[] args) throws Exception {

        loadAccounts();
        addInitialEvents();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/", Server::handleRequest);
        server.setExecutor(null);

        System.out.println("CampusConnect Server Started!");
        System.out.println("Open: http://localhost:8080");

        server.start();
    }

    // INITIAL EVENTS

    private static void addInitialEvents() {

        events.add(new Event(
                101, "Tech Fest 2026",
                "A campus technical festival.",
                "2026-10-15", "10:00",
                "Main Auditorium", "Technical", "Priya Sharma"));

        events.add(new Event(
                102, "Campus Cultural Fest",
                "Celebrate campus culture and talent.",
                "2026-11-05", "11:00",
                "College Ground", "Cultural", "Cultural Club"));

        events.add(new Event(
                103, "Web Development Workshop",
                "Learn web development fundamentals.",
                "2026-11-20", "14:00",
                "Computer Lab", "Workshop", "Computer Science Club"));
    }

    // REQUEST HANDLER

    private static void handleRequest(HttpExchange exchange)
            throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        System.out.println(method + " " + path);

        if (path.equals("/login") && method.equalsIgnoreCase("POST")) {
            handleLogin(exchange);
            return;
        }

        if (path.equals("/signup") && method.equalsIgnoreCase("POST")) {
            handleSignup(exchange);
            return;
        }

        if (path.equals("/organizer-signup")
                && method.equalsIgnoreCase("POST")) {
            handleOrganizerSignup(exchange);
            return;
        }

        if (path.equals("/student-profile")
                && method.equalsIgnoreCase("GET")) {
            handleStudentProfile(exchange);
            return;
        }

        if (path.equals("/events") && method.equalsIgnoreCase("GET")) {
            handleGetEvents(exchange);
            return;
        }

        if (path.equals("/create-event")
                && method.equalsIgnoreCase("POST")) {
            handleCreateEvent(exchange);
            return;
        }

        if (path.equals("/delete-event")
                && method.equalsIgnoreCase("POST")) {
            handleDeleteEvent(exchange);
            return;
        }

        if (path.equals("/register") && method.equalsIgnoreCase("POST")) {
            handleRegistration(exchange);
            return;
        }

        if (path.equals("/cancel-registration")
                && method.equalsIgnoreCase("POST")) {
            handleCancelRegistration(exchange);
            return;
        }

        if (path.equals("/my-events")
                && method.equalsIgnoreCase("GET")) {
            handleMyEvents(exchange);
            return;
        }

        if (path.equals("/registrations")
                && method.equalsIgnoreCase("GET")) {
            handleRegistrations(exchange);
            return;
        }

        if (path.equals("/mark-attendance")
                && method.equalsIgnoreCase("POST")) {
            handleMarkAttendance(exchange);
            return;
        }

        if (path.equals("/event-attendance")
                && method.equalsIgnoreCase("GET")) {
            handleEventAttendance(exchange);
            return;
        }

        // NOTIFICATION ROUTES

        if (path.equals("/notifications")
                && method.equalsIgnoreCase("GET")) {
            handleGetNotifications(exchange);
            return;
        }

        if (path.equals("/mark-notification-read")
                && method.equalsIgnoreCase("POST")) {
            handleMarkNotificationRead(exchange);
            return;
        }

        // HTML, CSS AND JAVASCRIPT FILE ROUTES

        Path file;

        switch (path) {

            case "/":
            case "/index.html":
                file = Path.of("../index.html");
                break;

            case "/login.html":
                file = Path.of("../login.html");
                break;

            case "/student-dashboard.html":
                file = Path.of("../student-dashboard.html");
                break;

            case "/student-details.html":
                file = Path.of("../student-details.html");
                break;

            case "/organizer-dashboard.html":
                file = Path.of("../organizer-dashboard.html");
                break;

            case "/events.html":
                file = Path.of("../events.html");
                break;

            case "/my-events.html":
                file = Path.of("../my-events.html");
                break;

            case "/attendance.html":
                file = Path.of("../attendance.html");
                break;

            case "/add-event.html":
                file = Path.of("../add-event.html");
                break;

            case "/manage-events.html":
                file = Path.of("../manage-events.html");
                break;

            case "/registrations.html":
                file = Path.of("../registrations.html");
                break;

            case "/notifications.html":
                file = Path.of("../notifications.html");
                break;

            // EVENT CALENDAR ROUTES

            case "/calendar.html":
                file = Path.of("../calendar.html");
                break;

            case "/calendar.css":
                file = Path.of("../calendar.css");
                break;

            case "/calendar.js":
                file = Path.of("../calendar.js");
                break;

            case "/style.css":
                file = Path.of("../style.css");
                break;

            case "/script.js":
                file = Path.of("../script.js");
                break;

            default:
                sendResponse(exchange, 404, "Page not found.");
                return;
        }

        if (!Files.exists(file)) {
            sendResponse(exchange, 404, "File not found: " + file);
            return;
        }

        byte[] content = Files.readAllBytes(file);

        String contentType = "text/plain; charset=UTF-8";

        if (path.endsWith(".html") || path.equals("/")) {
            contentType = "text/html; charset=UTF-8";
        } else if (path.endsWith(".css")) {
            contentType = "text/css; charset=UTF-8";
        } else if (path.endsWith(".js")) {
            contentType = "application/javascript; charset=UTF-8";
        }

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, content.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(content);
        }
    }

    // LOGIN

    private static void handleLogin(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        String email = getFormValue(body, "email")
                .trim().toLowerCase(Locale.ROOT);

        String password = getFormValue(body, "password");

        if (email.equals("rahul@college.edu")
                && password.equals("student123")) {

            sendJsonResponse(exchange, 200,
                    "{\"message\":\"Login successful.\","
                    + "\"role\":\"student\","
                    + "\"studentId\":1,"
                    + "\"name\":\"Rahul Kumar\","
                    + "\"email\":\"rahul@college.edu\"}");
            return;
        }

        if (email.equals("priya@college.edu")
                && password.equals("organizer123")) {

            sendJsonResponse(exchange, 200,
                    "{\"message\":\"Login successful.\","
                    + "\"role\":\"organizer\","
                    + "\"organizerId\":1,"
                    + "\"name\":\"Priya Sharma\","
                    + "\"email\":\"priya@college.edu\"}");
            return;
        }

        if (email.equals("admin@college.edu")
                && password.equals("admin123")) {

            sendJsonResponse(exchange, 200,
                    "{\"message\":\"Login successful.\","
                    + "\"role\":\"admin\"}");
            return;
        }

        StudentAccount student = studentAccounts.get(email);

        if (student != null && student.password.equals(password)) {

            sendJsonResponse(exchange, 200,
                    "{\"message\":\"Login successful.\","
                    + "\"role\":\"student\","
                    + "\"studentId\":" + student.studentId + ","
                    + "\"name\":\"" + jsonEscape(student.name) + "\","
                    + "\"email\":\"" + jsonEscape(student.email) + "\"}");
            return;
        }

        OrganizerAccount organizer = organizerAccounts.get(email);

        if (organizer != null
                && organizer.password.equals(password)) {

            sendJsonResponse(exchange, 200,
                    "{\"message\":\"Login successful.\","
                    + "\"role\":\"organizer\","
                    + "\"organizerId\":" + organizer.organizerId + ","
                    + "\"name\":\"" + jsonEscape(organizer.name) + "\","
                    + "\"email\":\"" + jsonEscape(organizer.email) + "\"}");
            return;
        }

        sendResponse(exchange, 401, "Invalid email or password.");
    }

    // STUDENT SIGNUP

    private static void handleSignup(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        String name = getFormValue(body, "name").trim();

        String email = getFormValue(body, "email")
                .trim().toLowerCase(Locale.ROOT);

        String password = getFormValue(body, "password");

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            sendResponse(exchange, 400,
                    "Please fill in all required fields.");
            return;
        }

        if (password.length() < 8) {
            sendResponse(exchange, 400,
                    "Password must contain at least 8 characters.");
            return;
        }

        if (email.equals("rahul@college.edu")
                || email.equals("priya@college.edu")
                || email.equals("admin@college.edu")
                || studentAccounts.containsKey(email)
                || organizerAccounts.containsKey(email)) {

            sendResponse(exchange, 409,
                    "An account with this email already exists.");
            return;
        }

        StudentAccount account = new StudentAccount(
                nextStudentId++, name, email, password);

        studentAccounts.put(email, account);

        try {
            saveAccounts();
        } catch (IOException e) {
            studentAccounts.remove(email);
            nextStudentId--;

            sendResponse(exchange, 500,
                    "Could not save account. Please try again.");
            return;
        }

        sendJsonResponse(exchange, 201,
                "{\"message\":\"Student account created successfully.\","
                + "\"studentId\":" + account.studentId + ","
                + "\"name\":\"" + jsonEscape(account.name) + "\","
                + "\"email\":\"" + jsonEscape(account.email) + "\"}");
    }

    // ORGANIZER SIGNUP

    private static void handleOrganizerSignup(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        String name = getFormValue(body, "name").trim();

        String email = getFormValue(body, "email")
                .trim().toLowerCase(Locale.ROOT);

        String password = getFormValue(body, "password");

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            sendResponse(exchange, 400,
                    "Please fill in all required fields.");
            return;
        }

        if (password.length() < 8) {
            sendResponse(exchange, 400,
                    "Password must contain at least 8 characters.");
            return;
        }

        if (email.equals("rahul@college.edu")
                || email.equals("priya@college.edu")
                || email.equals("admin@college.edu")
                || studentAccounts.containsKey(email)
                || organizerAccounts.containsKey(email)) {

            sendResponse(exchange, 409,
                    "An account with this email already exists.");
            return;
        }

        OrganizerAccount account = new OrganizerAccount(
                nextOrganizerId++, name, email, password);

        organizerAccounts.put(email, account);

        try {
            saveAccounts();
        } catch (IOException e) {
            organizerAccounts.remove(email);
            nextOrganizerId--;

            sendResponse(exchange, 500,
                    "Could not save account. Please try again.");
            return;
        }

        sendJsonResponse(exchange, 201,
                "{\"message\":\"Organizer account created successfully.\","
                + "\"organizerId\":" + account.organizerId + ","
                + "\"name\":\"" + jsonEscape(account.name) + "\","
                + "\"email\":\"" + jsonEscape(account.email) + "\"}");
    }

    // LOAD AND SAVE ACCOUNTS

    private static synchronized void loadAccounts() {

        if (!Files.exists(ACCOUNTS_FILE)) {
            return;
        }

        Properties properties = new Properties();

        try (java.io.InputStream input =
                     Files.newInputStream(ACCOUNTS_FILE)) {

            properties.load(input);

            int maxStudentId = 1;
            int maxOrganizerId = 1;

            for (String key : properties.stringPropertyNames()) {

                if (!key.startsWith("student.")
                        || !key.endsWith(".email")) {
                    continue;
                }

                String prefix = key.substring(
                        0, key.length() - ".email".length());

                String email = properties.getProperty(key, "");

                int id = Integer.parseInt(
                        properties.getProperty(prefix + ".id", "0"));

                String name = properties.getProperty(prefix + ".name", "");

                String password = properties.getProperty(
                        prefix + ".password", "");

                if (!email.isBlank() && id > 1
                        && !name.isBlank() && !password.isBlank()) {

                    studentAccounts.put(email,
                            new StudentAccount(id, name, email, password));

                    maxStudentId = Math.max(maxStudentId, id);
                }
            }

            for (String key : properties.stringPropertyNames()) {

                if (!key.startsWith("organizer.")
                        || !key.endsWith(".email")) {
                    continue;
                }

                String prefix = key.substring(
                        0, key.length() - ".email".length());

                String email = properties.getProperty(key, "");

                int id = Integer.parseInt(
                        properties.getProperty(prefix + ".id", "0"));

                String name = properties.getProperty(prefix + ".name", "");

                String password = properties.getProperty(
                        prefix + ".password", "");

                if (!email.isBlank() && id > 1
                        && !name.isBlank() && !password.isBlank()) {

                    organizerAccounts.put(email,
                            new OrganizerAccount(id, name, email, password));

                    maxOrganizerId = Math.max(maxOrganizerId, id);
                }
            }

            nextStudentId = maxStudentId + 1;
            nextOrganizerId = maxOrganizerId + 1;

            System.out.println("Loaded saved student and organizer accounts.");

        } catch (Exception e) {
            System.err.println(
                    "Could not load saved accounts: " + e.getMessage());
        }
    }

    private static synchronized void saveAccounts()
            throws IOException {

        Properties properties = new Properties();

        for (StudentAccount account : studentAccounts.values()) {

            String prefix = "student."
                    + Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(
                            account.email.getBytes(StandardCharsets.UTF_8));

            properties.setProperty(prefix + ".id",
                    Integer.toString(account.studentId));

            properties.setProperty(prefix + ".name", account.name);
            properties.setProperty(prefix + ".email", account.email);
            properties.setProperty(prefix + ".password", account.password);
        }

        for (OrganizerAccount account : organizerAccounts.values()) {

            String prefix = "organizer."
                    + Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(
                            account.email.getBytes(StandardCharsets.UTF_8));

            properties.setProperty(prefix + ".id",
                    Integer.toString(account.organizerId));

            properties.setProperty(prefix + ".name", account.name);
            properties.setProperty(prefix + ".email", account.email);
            properties.setProperty(prefix + ".password", account.password);
        }

        try (java.io.OutputStream output =
                     Files.newOutputStream(ACCOUNTS_FILE)) {

            properties.store(output, "CampusConnect registered accounts");
        }
    }

    // STUDENT PROFILE

    private static void handleStudentProfile(HttpExchange exchange)
            throws IOException {

        int studentId = getQueryInt(exchange, "studentId", -1);

        if (studentId == 1) {

            sendJsonResponse(exchange, 200,
                    "{\"studentId\":1,"
                    + "\"name\":\"Rahul Kumar\","
                    + "\"email\":\"rahul@college.edu\"}");
            return;
        }

        for (StudentAccount account : studentAccounts.values()) {

            if (account.studentId == studentId) {

                sendJsonResponse(exchange, 200,
                        "{\"studentId\":" + account.studentId + ","
                        + "\"name\":\"" + jsonEscape(account.name) + "\","
                        + "\"email\":\"" + jsonEscape(account.email) + "\"}");
                return;
            }
        }

        sendResponse(exchange, 404, "Student account not found.");
    }

    // GET ALL EVENTS

    private static void handleGetEvents(HttpExchange exchange)
            throws IOException {

        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < events.size(); i++) {

            if (i > 0) {
                json.append(",");
            }

            json.append(eventToJson(events.get(i)));
        }

        json.append("]");

        sendJsonResponse(exchange, 200, json.toString());
    }

    // CREATE EVENT

    private static void handleCreateEvent(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        String title = getFormValue(body, "title").trim();
        String description = getFormValue(body, "description").trim();
        String date = getFormValue(body, "date").trim();
        String time = getFormValue(body, "time").trim();
        String venue = getFormValue(body, "venue").trim();
        String category = getFormValue(body, "category").trim();
        String organizerName = getFormValue(body, "organizerName").trim();

        int eventFee;

        try {
            eventFee = Integer.parseInt(
                    getFormValue(body, "eventFee").trim());
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Please enter a valid event fee.");
            return;
        }

        int studentLimit;

        try {
            studentLimit = Integer.parseInt(
                    getFormValue(body, "studentLimit").trim());
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400,
                    "Please enter a valid maximum number of students.");
            return;
        }

        if (studentLimit < 1 || studentLimit > 100000) {
            sendResponse(exchange, 400,
                    "Student limit must be between 1 and 100000.");
            return;
        }

        if (eventFee < 0 || eventFee > 100000) {
            sendResponse(exchange, 400,
                    "Event fee must be between ₹0 and ₹100000.");
            return;
        }

        if (title.isBlank() || description.isBlank()
                || date.isBlank() || time.isBlank()
                || venue.isBlank() || category.isBlank()
                || organizerName.isBlank()) {

            sendResponse(exchange, 400,
                    "Please fill in all event fields.");
            return;
        }

        try {
            LocalDate.parse(date);
        } catch (Exception e) {
            sendResponse(exchange, 400,
                    "Please enter a valid event date.");
            return;
        }

        Event event = new Event(
                nextEventId++, title, description, date,
                time, venue, category, organizerName);

        events.add(event);

        eventFees.put(event.getEventId(), eventFee);
        eventStudentLimits.put(event.getEventId(), studentLimit);

        // SEND NOTIFICATION TO ALL STUDENTS
        createEventNotifications(event);

        sendResponse(exchange, 200,
                "Event published successfully. Event ID: "
                        + event.getEventId()
                        + ". Maximum students: " + studentLimit
                        + ". Notifications sent to students.");
    }

    // CREATE NOTIFICATIONS FOR ALL STUDENTS

    private static void createEventNotifications(Event event) {

        String title = "New Event Published: " + event.getTitle();

        String message = "A new " + event.getCategory()
                + " event has been published by "
                + event.getOrganizerName() + ".";

        String createdAt = LocalDateTime.now().toString();

        // Demo student
        addNotification(
                1, event, title, message, createdAt);

        // All registered student accounts
        for (StudentAccount student : studentAccounts.values()) {

            addNotification(
                    student.studentId,
                    event,
                    title,
                    message,
                    createdAt);
        }

        System.out.println(
                "Notifications created for event: " + event.getTitle());
    }

    private static void addNotification(
            int studentId,
            Event event,
            String title,
            String message,
            String createdAt) {

        notifications.add(new Notification(
                nextNotificationId++,
                studentId,
                event.getEventId(),
                title,
                message,
                event.getDate(),
                event.getVenue(),
                createdAt
        ));
    }

    // DELETE EVENT

    private static void handleDeleteEvent(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        int eventId;

        try {
            eventId = Integer.parseInt(
                    getFormValue(body, "eventId"));
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid event ID.");
            return;
        }

        Event found = findEvent(eventId);

        if (found == null) {
            sendResponse(exchange, 404, "Event not found.");
            return;
        }

        events.remove(found);

        registrations.removeIf(r -> r.getEventId() == eventId);

        attendanceRecords.removeIf(a -> a.getEventId() == eventId);

        eventFees.remove(eventId);
        eventStudentLimits.remove(eventId);

        sendResponse(exchange, 200, "Event deleted successfully.");
    }

    // EVENT JSON

    private static String eventToJson(Event event) {

        int studentLimit = eventStudentLimits.getOrDefault(
                event.getEventId(), 100000);

        int registeredCount = 0;

        for (Registration registration : registrations) {

            if (registration.getEventId() == event.getEventId()) {
                registeredCount++;
            }
        }

        int availableSeats = Math.max(
                0, studentLimit - registeredCount);

        return "{"
                + "\"eventId\":" + event.getEventId() + ","
                + "\"title\":\"" + jsonEscape(event.getTitle()) + "\","
                + "\"description\":\"" + jsonEscape(event.getDescription()) + "\","
                + "\"date\":\"" + jsonEscape(event.getDate()) + "\","
                + "\"time\":\"" + jsonEscape(event.getTime()) + "\","
                + "\"venue\":\"" + jsonEscape(event.getVenue()) + "\","
                + "\"category\":\"" + jsonEscape(event.getCategory()) + "\","
                + "\"organizerName\":\""
                + jsonEscape(event.getOrganizerName()) + "\","
                + "\"studentLimit\":" + studentLimit + ","
                + "\"registeredCount\":" + registeredCount + ","
                + "\"availableSeats\":" + availableSeats + ","
                + "\"eventFee\":"
                + eventFees.getOrDefault(event.getEventId(), 0)
                + "}";
    }

    private static Event findEvent(int eventId) {

        for (Event event : events) {

            if (event.getEventId() == eventId) {
                return event;
            }
        }

        return null;
    }

    // REGISTER STUDENT FOR EVENT

    private static void handleRegistration(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        int studentId;

        String studentText = getFormValue(body, "studentId");
        String eventName = getFormValue(body, "eventName");
        String eventIdText = getFormValue(body, "eventId");

        try {
            studentId = Integer.parseInt(studentText);
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid student ID.");
            return;
        }

        if (!studentExists(studentId)) {
            sendResponse(exchange, 404, "Student account not found.");
            return;
        }

        Event event = null;

        if (!eventIdText.isBlank()) {

            try {
                event = findEvent(Integer.parseInt(eventIdText));
            } catch (NumberFormatException ignored) {
                event = null;
            }

        } else {

            for (Event item : events) {

                if (item.getTitle().equals(eventName)) {
                    event = item;
                    break;
                }
            }
        }

        if (event == null) {
            sendResponse(exchange, 400, "Event not found.");
            return;
        }

        for (Registration r : registrations) {

            if (r.getStudentId() == studentId
                    && r.getEventId() == event.getEventId()) {

                sendResponse(exchange, 409,
                        "You are already registered for "
                                + event.getTitle() + ".");
                return;
            }
        }

        int studentLimit = eventStudentLimits.getOrDefault(
                event.getEventId(), 100000);

        int registeredCount = 0;

        for (Registration r : registrations) {

            if (r.getEventId() == event.getEventId()) {
                registeredCount++;
            }
        }

        if (registeredCount >= studentLimit) {

            sendResponse(exchange, 409,
                    "Registration is full for " + event.getTitle()
                            + ". Maximum students: " + studentLimit);
            return;
        }

        registrations.add(new Registration(
                nextRegistrationId++,
                studentId,
                event.getEventId(),
                LocalDate.now().toString()));

        int seatsRemaining = studentLimit - registeredCount - 1;

        sendResponse(exchange, 200,
                "Registration successful for "
                        + event.getTitle()
                        + ". Seats remaining: " + seatsRemaining);
    }

    // CHECK STUDENT

    private static boolean studentExists(int studentId) {

        if (studentId == 1) {
            return true;
        }

        for (StudentAccount account : studentAccounts.values()) {

            if (account.studentId == studentId) {
                return true;
            }
        }

        return false;
    }

    // GET STUDENT NAME

    private static String studentName(int studentId) {

        if (studentId == 1) {
            return "Rahul Kumar";
        }

        for (StudentAccount account : studentAccounts.values()) {

            if (account.studentId == studentId) {
                return account.name;
            }
        }

        return "Unknown Student";
    }

    // GET STUDENT EMAIL

    private static String studentEmail(int studentId) {

        if (studentId == 1) {
            return "rahul@college.edu";
        }

        for (StudentAccount account : studentAccounts.values()) {

            if (account.studentId == studentId) {
                return account.email;
            }
        }

        return "";
    }

    // CANCEL REGISTRATION

    private static void handleCancelRegistration(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        int studentId;
        int eventId;

        try {
            studentId = Integer.parseInt(
                    getFormValue(body, "studentId"));

            eventId = Integer.parseInt(
                    getFormValue(body, "eventId"));

        } catch (NumberFormatException e) {

            sendResponse(exchange, 400,
                    "Invalid student or event ID.");
            return;
        }

        Registration found = null;

        for (Registration r : registrations) {

            if (r.getStudentId() == studentId
                    && r.getEventId() == eventId) {

                found = r;
                break;
            }
        }

        if (found == null) {
            sendResponse(exchange, 404, "Registration not found.");
            return;
        }

        registrations.remove(found);

        sendResponse(exchange, 200,
                "Registration cancelled for "
                        + getEventName(eventId) + ".");
    }

    // GET STUDENT'S REGISTERED EVENTS

    private static void handleMyEvents(HttpExchange exchange)
            throws IOException {

        int studentId = getQueryInt(exchange, "studentId", 1);

        if (!studentExists(studentId)) {
            sendResponse(exchange, 404, "Student account not found.");
            return;
        }

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        for (Registration r : registrations) {

            if (r.getStudentId() != studentId) {
                continue;
            }

            Event event = findEvent(r.getEventId());

            if (event == null) {
                continue;
            }

            if (!first) {
                json.append(",");
            }

            first = false;

            json.append("{")
                    .append("\"eventId\":")
                    .append(event.getEventId()).append(",")
                    .append("\"eventName\":\"")
                    .append(jsonEscape(event.getTitle())).append("\",")
                    .append("\"date\":\"")
                    .append(jsonEscape(event.getDate())).append("\",")
                    .append("\"time\":\"")
                    .append(jsonEscape(event.getTime())).append("\",")
                    .append("\"venue\":\"")
                    .append(jsonEscape(event.getVenue())).append("\",")
                    .append("\"category\":\"")
                    .append(jsonEscape(event.getCategory())).append("\",")
                    .append("\"organizer\":\"")
                    .append(jsonEscape(event.getOrganizerName())).append("\",")
                    .append("\"registrationDate\":\"")
                    .append(jsonEscape(r.getRegistrationDate()))
                    .append("\"}");
        }

        json.append("]");

        sendJsonResponse(exchange, 200, json.toString());
    }

    // GET REGISTRATIONS FOR AN EVENT

    private static void handleRegistrations(HttpExchange exchange)
            throws IOException {

        int eventId = getQueryInt(exchange, "eventId", -1);

        if (findEvent(eventId) == null) {
            sendResponse(exchange, 404, "Event not found.");
            return;
        }

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        for (Registration r : registrations) {

            if (r.getEventId() != eventId) {
                continue;
            }

            if (!first) {
                json.append(",");
            }

            first = false;

            json.append("{")
                    .append("\"registrationId\":")
                    .append(r.getRegistrationId()).append(",")
                    .append("\"studentId\":")
                    .append(r.getStudentId()).append(",")
                    .append("\"studentName\":\"")
                    .append(jsonEscape(studentName(r.getStudentId())))
                    .append("\",")
                    .append("\"studentEmail\":\"")
                    .append(jsonEscape(studentEmail(r.getStudentId())))
                    .append("\",")
                    .append("\"registrationDate\":\"")
                    .append(jsonEscape(r.getRegistrationDate()))
                    .append("\"}");
        }

        json.append("]");

        sendJsonResponse(exchange, 200, json.toString());
    }

    // MARK ATTENDANCE

    private static void handleMarkAttendance(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        int studentId;
        int eventId;

        String status = getFormValue(body, "status");

        try {
            studentId = Integer.parseInt(
                    getFormValue(body, "studentId"));

            eventId = Integer.parseInt(
                    getFormValue(body, "eventId"));

        } catch (NumberFormatException e) {

            sendResponse(exchange, 400,
                    "Invalid student or event ID.");
            return;
        }

        if (!status.equals("Present") && !status.equals("Absent")) {

            sendResponse(exchange, 400,
                    "Status must be Present or Absent.");
            return;
        }

        boolean registered = false;

        for (Registration r : registrations) {

            if (r.getStudentId() == studentId
                    && r.getEventId() == eventId) {

                registered = true;
                break;
            }
        }

        if (!registered) {

            sendResponse(exchange, 404,
                    "Student is not registered for this event.");
            return;
        }

        Attendance existing = null;

        for (Attendance a : attendanceRecords) {

            if (a.getStudentId() == studentId
                    && a.getEventId() == eventId) {

                existing = a;
                break;
            }
        }

        if (existing != null) {

            existing.setStatus(status);

        } else {

            attendanceRecords.add(new Attendance(
                    nextAttendanceId++,
                    studentId,
                    eventId,
                    status,
                    LocalDate.now().toString()));
        }

        sendResponse(exchange, 200,
                "Attendance saved: " + status + ".");
    }

    // GET ATTENDANCE FOR AN EVENT

    private static void handleEventAttendance(HttpExchange exchange)
            throws IOException {

        int eventId = getQueryInt(exchange, "eventId", -1);

        if (findEvent(eventId) == null) {

            sendResponse(exchange, 400,
                    "Please provide a valid event ID.");
            return;
        }

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        for (Registration r : registrations) {

            if (r.getEventId() != eventId) {
                continue;
            }

            String status = "Not Marked";

            for (Attendance a : attendanceRecords) {

                if (a.getStudentId() == r.getStudentId()
                        && a.getEventId() == eventId) {

                    status = a.getStatus();
                    break;
                }
            }

            if (!first) {
                json.append(",");
            }

            first = false;

            json.append("{")
                    .append("\"studentId\":")
                    .append(r.getStudentId()).append(",")
                    .append("\"studentName\":\"")
                    .append(jsonEscape(studentName(r.getStudentId())))
                    .append("\",")
                    .append("\"studentEmail\":\"")
                    .append(jsonEscape(studentEmail(r.getStudentId())))
                    .append("\",")
                    .append("\"eventId\":")
                    .append(eventId).append(",")
                    .append("\"eventName\":\"")
                    .append(jsonEscape(getEventName(eventId))).append("\",")
                    .append("\"status\":\"")
                    .append(jsonEscape(status)).append("\"}");
        }

        json.append("]");

        sendJsonResponse(exchange, 200, json.toString());
    }

    // NOTIFICATION SYSTEM

    // GET NOTIFICATIONS FOR A STUDENT

    private static void handleGetNotifications(HttpExchange exchange)
            throws IOException {

        int studentId = getQueryInt(exchange, "studentId", -1);

        if (!studentExists(studentId)) {
            sendResponse(exchange, 404, "Student account not found.");
            return;
        }

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        for (Notification notification : notifications) {

            if (notification.getStudentId() != studentId) {
                continue;
            }

            if (!first) {
                json.append(",");
            }

            first = false;

            json.append(notificationToJson(notification));
        }

        json.append("]");

        sendJsonResponse(exchange, 200, json.toString());
    }

    // MARK A NOTIFICATION AS READ

    private static void handleMarkNotificationRead(HttpExchange exchange)
            throws IOException {

        String body = readBody(exchange);

        int studentId;
        int notificationId;

        try {

            studentId = Integer.parseInt(
                    getFormValue(body, "studentId"));

            notificationId = Integer.parseInt(
                    getFormValue(body, "notificationId"));

        } catch (NumberFormatException e) {

            sendResponse(exchange, 400,
                    "Invalid student ID or notification ID.");
            return;
        }

        if (!studentExists(studentId)) {
            sendResponse(exchange, 404, "Student account not found.");
            return;
        }

        for (Notification notification : notifications) {

            if (notification.getNotificationId() == notificationId
                    && notification.getStudentId() == studentId) {

                notification.markAsRead();

                sendResponse(exchange, 200,
                        "Notification marked as read.");
                return;
            }
        }

        sendResponse(exchange, 404, "Notification not found.");
    }

    // CONVERT NOTIFICATION INTO JSON

    private static String notificationToJson(
            Notification notification) {

        return "{"
                + "\"notificationId\":"
                + notification.getNotificationId() + ","

                + "\"studentId\":"
                + notification.getStudentId() + ","

                + "\"eventId\":"
                + notification.getEventId() + ","

                + "\"title\":\""
                + jsonEscape(notification.getTitle()) + "\","

                + "\"message\":\""
                + jsonEscape(notification.getMessage()) + "\","

                + "\"eventDate\":\""
                + jsonEscape(notification.getEventDate()) + "\","

                + "\"venue\":\""
                + jsonEscape(notification.getVenue()) + "\","

                + "\"createdAt\":\""
                + jsonEscape(notification.getCreatedAt()) + "\","

                + "\"read\":"
                + notification.isRead()

                + "}";
    }

    // GET EVENT NAME

    private static String getEventName(int id) {

        Event event = findEvent(id);

        return event == null ? "Unknown Event" : event.getTitle();
    }

    // QUERY HELPER

    private static int getQueryInt(
            HttpExchange exchange,
            String key,
            int defaultValue) {

        String query = exchange.getRequestURI().getQuery();

        if (query == null) {
            return defaultValue;
        }

        for (String part : query.split("&")) {

            String[] pieces = part.split("=", 2);

            if (pieces.length == 2 && pieces[0].equals(key)) {

                try {
                    return Integer.parseInt(
                            URLDecoder.decode(
                                    pieces[1], StandardCharsets.UTF_8));

                } catch (NumberFormatException ignored) {
                    return defaultValue;
                }
            }
        }

        return defaultValue;
    }

    // JSON ESCAPE

    private static String jsonEscape(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // READ REQUEST BODY

    private static String readBody(HttpExchange exchange)
            throws IOException {

        return new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    // GET FORM VALUE

    private static String getFormValue(String body, String key) {

        for (String parameter : body.split("&")) {

            String[] parts = parameter.split("=", 2);

            if (parts.length == 2 && parts[0].equals(key)) {

                return URLDecoder.decode(
                        parts[1], StandardCharsets.UTF_8);
            }
        }

        return "";
    }

    // RESPONSE HELPERS

    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String message) throws IOException {

        byte[] response = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "text/plain; charset=UTF-8");

        exchange.sendResponseHeaders(statusCode, response.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(response);
        }
    }

    private static void sendJsonResponse(
            HttpExchange exchange,
            int statusCode,
            String json) throws IOException {

        byte[] response = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(statusCode, response.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(response);
        }
    }
}