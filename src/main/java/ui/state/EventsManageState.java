package ui.state;

import java.util.Locale;

import org.springframework.context.ApplicationContext;

import domainModel.Event;
import domainModel.EventRating;

import domainServices.AuditoriumService;
import domainServices.EventService;

/**
 * State for managing events
 *
 * @author Yuriy_Tkach
 */
public class EventsManageState extends DomainManageState<Event, EventService> {

    private AuditoriumService auditoriumService;

    public EventsManageState(EventService eventsSvc,
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

        Event event = new Event();
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
            default:
                System.err.println("Unknown action");
        }
    }

    private void manageEventInfo() {
        int id = readIntInput("Input event id: ");

        Event event = service.getById(Long.valueOf(id));
        if (event == null) {
            System.out.println("Not found (searched for " + id + ")");
        } else {
            printDelimiter();

            AbstractState manageState = new SingleEventManageState(event, service, auditoriumService);
            manageState.run();
        }
    }

    private void findEventByName() {
        String name = readStringInput("Input event name: ");
        Event event = service.getByName(name);
        if (event == null) {
            System.out.println("Not found (searched for " + name + ")");
        } else {
            printObject(event);
        }
    }

}

