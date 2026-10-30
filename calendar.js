// CampusConnect Event Calendar

let allEvents = [];
let currentDate = new Date();
let selectedDate = new Date();

const monthYear = document.getElementById("monthYear");
const calendarDays = document.getElementById("calendarDays");
const selectedDateHeading = document.getElementById("selectedDate");
const selectedEvents = document.getElementById("selectedEvents");
const upcomingEvents = document.getElementById("upcomingEvents");


// Convert event date into YYYY-MM-DD format
function getDateKey(dateValue) {
    if (!dateValue) return "";

    const value = String(dateValue).trim();

    // Handles dates in YYYY-MM-DD format
    const match = value.match(/^(\d{4})-(\d{1,2})-(\d{1,2})/);

    if (match) {
        return `${match[1]}-${String(match[2]).padStart(2, "0")}-${String(match[3]).padStart(2, "0")}`;
    }

    const date = new Date(value);

    if (isNaN(date.getTime())) return "";

    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}


// Format date for display
function formatDate(date) {
    return date.toLocaleDateString("en-IN", {
        day: "numeric",
        month: "long",
        year: "numeric"
    });
}


// Get event title
function getEventTitle(event) {
    return event.eventName || event.name || event.title || "Campus Event";
}


// Get event date
function getEventDate(event) {
    return event.date || event.eventDate || "";
}


// Get event venue
function getEventVenue(event) {
    return event.venue || event.location || "Venue not specified";
}


// Get event category
function getEventCategory(event) {
    return event.category || event.eventCategory || "Campus Event";
}


// Display the monthly calendar
function renderCalendar() {

    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    const firstDay = new Date(year, month, 1).getDay();
    const totalDays = new Date(year, month + 1, 0).getDate();

    monthYear.textContent = currentDate.toLocaleDateString("en-IN", {
        month: "long",
        year: "numeric"
    });

    calendarDays.innerHTML = "";

    // Empty spaces before the first day
    for (let i = 0; i < firstDay; i++) {
        const emptyDay = document.createElement("div");
        emptyDay.className = "calendar-day empty";
        calendarDays.appendChild(emptyDay);
    }

    const today = new Date();

    for (let day = 1; day <= totalDays; day++) {

        const date = new Date(year, month, day);
        const dateKey = getDateKey(date);

        const dayElement = document.createElement("div");
        dayElement.className = "calendar-day";
        dayElement.textContent = day;

        // Highlight today's date
        if (
            day === today.getDate() &&
            month === today.getMonth() &&
            year === today.getFullYear()
        ) {
            dayElement.classList.add("today");
        }

        // Highlight selected date
        if (dateKey === getDateKey(selectedDate)) {
            dayElement.classList.add("selected");
        }

        // Show event indicator
        const hasEvent = allEvents.some(event =>
            getDateKey(getEventDate(event)) === dateKey
        );

        if (hasEvent) {
            dayElement.classList.add("has-event");
        }

        dayElement.addEventListener("click", function () {
            selectedDate = date;
            renderCalendar();
            showSelectedEvents();
        });

        calendarDays.appendChild(dayElement);
    }
}


// Display events for selected date
function showSelectedEvents() {

    const selectedKey = getDateKey(selectedDate);

    selectedDateHeading.textContent = formatDate(selectedDate);

    const eventsForDate = allEvents.filter(event =>
        getDateKey(getEventDate(event)) === selectedKey
    );

    selectedEvents.innerHTML = "";

    if (eventsForDate.length === 0) {
        selectedEvents.innerHTML =
            '<p class="empty-message">No events scheduled for this date.</p>';
        return;
    }

    eventsForDate.forEach(event => {

        const card = document.createElement("div");
        card.className = "event-item";

        const title = document.createElement("h3");
        title.textContent = getEventTitle(event);

        const category = document.createElement("p");
        category.textContent = "Category: " + getEventCategory(event);

        const venue = document.createElement("p");
        venue.textContent = "Venue: " + getEventVenue(event);

        const time = document.createElement("p");
        time.textContent = "Time: " + (event.time || "To be announced");

        card.appendChild(title);
        card.appendChild(category);
        card.appendChild(venue);
        card.appendChild(time);

        selectedEvents.appendChild(card);
    });
}


// Display upcoming events
function showUpcomingEvents() {

    upcomingEvents.innerHTML = "";

    const todayKey = getDateKey(new Date());

    const upcoming = allEvents
        .filter(event => {
            const eventDate = getDateKey(getEventDate(event));
            return eventDate && eventDate >= todayKey;
        })
        .sort((a, b) =>
            getDateKey(getEventDate(a)).localeCompare(
                getDateKey(getEventDate(b))
            )
        )
        .slice(0, 6);

    if (upcoming.length === 0) {
        upcomingEvents.innerHTML =
            '<p class="empty-message">No upcoming events available.</p>';
        return;
    }

    upcoming.forEach(event => {

        const card = document.createElement("div");
        card.className = "upcoming-card";

        const title = document.createElement("h3");
        title.textContent = getEventTitle(event);

        const date = document.createElement("p");
        date.textContent = "📅 " + getEventDate(event);

        const venue = document.createElement("p");
        venue.textContent = "📍 " + getEventVenue(event);

        const category = document.createElement("p");
        category.textContent = "Category: " + getEventCategory(event);

        card.appendChild(title);
        card.appendChild(date);
        card.appendChild(venue);
        card.appendChild(category);

        upcomingEvents.appendChild(card);
    });
}


// Load events from Java backend
async function loadCalendarEvents() {

    try {

        upcomingEvents.innerHTML = "<p>Loading events...</p>";

        const response = await fetch("/events");

        if (!response.ok) {
            throw new Error("Unable to fetch events from server.");
        }

        const data = await response.json();

        allEvents = Array.isArray(data)
            ? data
            : Array.isArray(data.events)
                ? data.events
                : [];

        renderCalendar();
        showSelectedEvents();
        showUpcomingEvents();

    } catch (error) {

        console.error("Calendar loading error:", error);

        calendarDays.innerHTML =
            '<p class="empty-message">Unable to load calendar.</p>';

        selectedEvents.innerHTML =
            '<p class="empty-message">Please check whether the Java server is running.</p>';

        upcomingEvents.innerHTML =
            '<p class="empty-message">Unable to load upcoming events.</p>';
    }
}


// Previous month
document.getElementById("prevMonth").addEventListener("click", function () {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar();
});


// Next month
document.getElementById("nextMonth").addEventListener("click", function () {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar();
});


// Initial calendar loading
document.addEventListener("DOMContentLoaded", function () {

    renderCalendar();
    loadCalendarEvents();

});


// Refresh events every 15 seconds
setInterval(loadCalendarEvents, 15000);