
async function loadHomeEvents() {
    const container = document.getElementById("homeEvents");

    if (!container) {
        return;
    }

    container.innerHTML = "<p>Loading upcoming events...</p>";

    try {
        const response = await fetch("/events");

        if (!response.ok) {
            throw new Error("Server returned an error.");
        }

        const events = await response.json();

        container.innerHTML = "";

        if (!Array.isArray(events) || events.length === 0) {
            container.innerHTML = "<p>No events available yet.</p>";
            return;
        }

        events.forEach(function(event) {
            const card = document.createElement("div");
            card.className = "event-card";

            const category = document.createElement("div");
            category.className = "event-category";
            category.textContent =
                event.category || event.eventCategory || "Campus Event";

            const title = document.createElement("h3");
            title.textContent =
                event.eventName ||
                event.name ||
                event.title ||
                event.eventTitle ||
                "Campus Event";

            const description = document.createElement("p");
            description.textContent =
                event.description ||
                event.eventDescription ||
                "Join this exciting campus event.";

            const details = document.createElement("div");
            details.className = "event-details";

            const date = document.createElement("span");
            date.textContent =
                "📅 " +
                (event.date || event.eventDate || "Date to be announced");

            const venue = document.createElement("span");
            venue.textContent =
                "📍 " +
                (event.venue ||
                 event.location ||
                 event.eventLocation ||
                 "Venue to be announced");

            const button = document.createElement("a");
            button.className = "register-button";
            button.href = "/login.html";
            button.textContent = "View Event";

            details.appendChild(date);
            details.appendChild(venue);

            card.appendChild(category);
            card.appendChild(title);
            card.appendChild(description);
            card.appendChild(details);
            card.appendChild(button);

            container.appendChild(card);
        });

    } catch (error) {
        console.error("Unable to load home page events:", error);

        container.innerHTML =
            "<p>Unable to load events. Please check that the Java server is running.</p>";
    }
}

document.addEventListener("DOMContentLoaded", loadHomeEvents);
