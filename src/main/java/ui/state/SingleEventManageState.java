package ui.state;

import java.time.LocalDateTime;
        import java.util.List;
        import java.util.stream.Collectors;

        import domainModel.Auditorium;
        import domainModel.Event;

        import domainServices.AuditoriumService;
        import domainServices.EventService;

public class SingleEventManageState extends AbstractState {

    private Event event;
    private EventService eventService;
    private AuditoriumService auditoriumService;

    public SingleEventManageState(Event event, EventService eventService, AuditoriumService auditoriumService) {
        this.event = event;
        this.eventService = eventService;
        this.auditoriumService = auditoriumService;
    }

    @Override
    protected void printDefaultInformation() {
        System.out.println("Information about Event: " + event.getName());
    }

    @Override
    protected int printMainActions() {
        System.out.println(" 1) View air dates");
        System.out.println(" 2) Add air date");
        System.out.println(" 3) View assigned auditoriums");
        System.out.println(" 4) Assign auditorium");
        return 4;
    }

    @Override
    protected void runAction(int action) {
        switch (action) {
            case 1:
                viewAirDates();
                break;
            case 2:
                addAirDate();
                break;
            case 3:
                viewAssignedAuditoriums();
                break;
            case 4:
                assignAuditorium();
                break;
            default:
                System.err.println("Unknown action");
        }
    }

    private void assignAuditorium() {
        System.out.println("Select auditorium:");
        List<Auditorium> list = auditoriumService.getAll().stream().collect(Collectors.toList());
        for (int i = 0; i < list.size(); i++) {
            System.out.println("[" + (i+1) + "] " + list.get(i).getName());
        }
        int auditoriumIndex = readIntInput("Input index: ", list.size()) - 1;
        Auditorium aud = list.get(auditoriumIndex);
        System.out.println("Assigning auditorium: " + aud.getName());
        List<LocalDateTime> datesList = event.getAirDates().stream().collect(Collectors.toList());
        for (int i = 0; i < datesList.size(); i++) {
            System.out.println("[" + (i+1) + "] " + formatDateTime(datesList.get(i)));
        }
        int dateTimeIndex = readIntInput("Input air dateTime index: ", datesList.size()) - 1;
        LocalDateTime dt = datesList.get(dateTimeIndex);
        if (event.assignAuditorium(dt, aud)) {
            System.out.println("Assigned auditorium for air dateTime: " + formatDateTime(dt));
        } else {
            System.err.println("Failed to assign for air dateTime: " + formatDateTime(dt));
        }
    }

    private void viewAssignedAuditoriums() {
        System.out.println("Event airs in: ");
        event.getAuditoriums().entrySet()
                .forEach(e -> System.out.println(formatDateTime(e.getKey()) + " " + e.getValue().getName()));
    }

    private void addAirDate() {
        LocalDateTime airDate = readDateTimeInput("Air date (" + DATE_TIME_INPUT_PATTERN + "): ");
        event.addAirDateTime(airDate);
        eventService.save(event);
    }

    private void viewAirDates() {
        System.out.println("Event airs on: ");
        event.getAirDates().forEach(dt -> System.out.println(formatDateTime(dt)));
    }

}

