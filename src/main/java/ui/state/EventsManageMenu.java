package ui.state;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import domainModel.Event;
import domainModel.EventRating;

import domainServices.AuditoriumService;
import domainServices.EventService;

/**
 * State for managing events
 *
 * @author Yuriy_Tkach
 */
public class EventsManageMenu extends DomainManageMenu<Event, EventService> {

    private AuditoriumService auditoriumService;

    public EventsManageMenu(EventService eventsSvc,
                            AuditoriumService auditoriumSvc) {
        super(eventsSvc);
        this.auditoriumService = auditoriumSvc;
    }

    @Override
    protected String getObjectName() {
        return Event.class.getSimpleName().toLowerCase(Locale.ROOT);
    }

    @Override
    protected void printObject(Event event) {
        System.out.println("[" + event.getId() + "] " + event.getName() + " (Rating: " + event.getRating() + ", Price: "
                + event.getBasePrice() + ")");
    }

    @Override
    protected Event createObject() {
        System.out.println("Adding event");
        String name = readStringInput("Name: ");
        EventRating rating = readEventRating();
        double basePrice = readDoubleInput("Base price: ");

        Event event = service.create();
        event.setName(name);
        event.setRating(rating);
        event.setBasePrice(basePrice);

        return event;
    }

    private EventRating readEventRating() {
        EventRating rating = null;
        do {
            String str = readStringInput("Rating (LOW, MID, HIGH): ");
            try {
                rating = EventRating.valueOf(str);
            } catch (Exception e) {
                rating = null;
            }
        } while (rating == null);
        return rating;
    }

    @Override
    protected int printSubActions(int maxDefaultActions) {
        int index = maxDefaultActions;
        System.out.println(" " + (++index) + ") Find event by name");
        System.out.println(" " + (++index) + ") Manage event info (air dates, auditoriums)");
        System.out.println(" " + (++index) + ") Returns events for specified date range");

        return index - maxDefaultActions;
    }

    @Override
    protected void runSubAction(int action, int maxDefaultActions) {
        int index = action - maxDefaultActions;
        switch (index) {
            case 1:
                findEventByName();
                break;
            case 2:
                manageEventInfo();
                break;
            case 3:
                getEventsRange();
                break;
            default:
                System.err.println("Unknown action");
        }
    }

    private void manageEventInfo() {
        int id = readIntInput("Input event id: ");

        Optional<Event> event = service.getById(Long.valueOf(id));
        if (event == null || !event.isPresent()) {
            System.out.println("Not found (searched for " + id + ")");
        } else {
            printDelimiter();

            AbstractMenu manageState = new SingleEventManageMenu(event.get(), service, auditoriumService);
            manageState.run();
        }
    }

    private void findEventByName() {
        String name = readStringInput("Input event name: ");
        Optional<Event> event = service.getByName(name);
        if (event == null || !event.isPresent()) {
            System.out.println("Not found (searched for " + name + ")");
        } else {
            printObject(event.get());
        }
    }

    private void getEventsRange(){

        LocalDateTime fromDate = readDateTimeInput("From date (" + DATE_TIME_INPUT_PATTERN + "): ");
        LocalDateTime toDate = readDateTimeInput("To date (" + DATE_TIME_INPUT_PATTERN + "): ");

        Collection<Event> events = service.getForDateRange(fromDate, toDate);

        for(Event e : events){
            printObject(e);
        }
    }
}

