package ui.state;


import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
        import java.util.stream.Collectors;

import domainModel.Auditorium;
        import domainModel.DomainObject;
        import domainModel.Event;
        import domainModel.Ticket;
        import domainModel.User;

import domainServices.*;
import exceptions.ApplicationException;

public class BookingManageMenu extends AbstractMenu {

    private final BookingService bookingService;
    private final UserService userService;
    private final EventService eventService;
    private final AuditoriumService audService;

    public BookingManageMenu(BookingService bookingSvc,
                             UserService userSvc,
                             EventService eventSvc,
                             AuditoriumService audService) {
        this.bookingService = bookingSvc;
        this.userService = userSvc;
        this.eventService = eventSvc;
        this.audService = audService;
    }

    @Override
    protected void printDefaultInformation() {
        System.out.println("Lets book tickets!");
    }

    @Override
    protected int printMainActions() {
        System.out.println(" 1) Get tickets price");
        System.out.println(" 2) Book tickets");
        System.out.println(" 3) Get booked tickets");
        return 4;
    }

    @Override
    protected void runAction(int action) {
        switch (action) {
            case 1:
                getTicketsPrice();
                break;
            case 2:
                bookTickets();
                break;
            case 3:
                getBookedTickets();
                break;
            default:
                System.err.println("Unknown action");
        }
    }

    private void getBookedTickets() {
        System.out.println("> Select event: ");
        Optional<Event> eventOpt = selectDomainObject(eventService, e -> e.getName());
        if (!hasValue(eventOpt)) {
            System.err.println("No event found");
            return;
        }

        Event event = eventOpt.get();

        System.out.println("> Select air dates: ");
        LocalDateTime airDate = selectAirDate(event.getAirDates());

        printDelimiter();
        Set<Ticket> bookedTickets = bookingService.getPurchasedTicketsForEvent(event, airDate);
        bookedTickets.forEach(t -> System.out.println("Seat " + t.getSeat() + "\t for " + t.getUser().getEmail()));
    }

    private void bookTickets() {
        System.out.println("> Select event: ");
        final Optional<Event> eventOpt = selectDomainObject(eventService, e -> e.getName());
        if (!hasValue(eventOpt)) {
            System.err.println("No event found");
            return;
        }

        Event event = eventOpt.get();

        System.out.println("> Select air dates: ");
        final LocalDateTime airDate = selectAirDate(event.getAirDates());
        System.out.println("> Select seats: ");
        final Set<Long> seats;
        try {
            seats = selectSeats(event, airDate);
        } catch (ApplicationException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("> Select user: ");
        final User userForBooking;

        Optional<User> userOpt = selectDomainObject(userService, u -> u.getFirstName() + " " + u.getLastName());
        if (userOpt == null || !userOpt.isPresent()) {
            System.out.println("No user found. Input user info for booking: ");
            String email = readStringInput("Email: ");
            String firstName = readStringInput("First name: ");
            String lastName = readStringInput("Last name: ");
            LocalDateTime birthDate = readDateTimeInput("Birth date: ");

            userForBooking = userService.createNew(birthDate);
            userForBooking.setEmail(email);
            userForBooking.setFirstName(firstName);
            userForBooking.setLastName(lastName);
        } else {
            userForBooking = userOpt.get();
        }

        Set<Ticket> ticketsToBook = seats.stream().map(seat -> bookingService.createTicket(userForBooking, event, airDate, seat)).collect(Collectors.toSet());
        bookingService.bookTickets(ticketsToBook);
        double price = 0;
        try {
            price = bookingService.getTicketsPrice(event, airDate, userOpt.get(), seats);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }

        System.out.println("Tickets booked! Total price: " + price);
    }

    private void getTicketsPrice() {
        System.out.println("> Select event: ");
        Optional<Event> eventOpt = selectDomainObject(eventService, e -> e.getName());
        if (!hasValue(eventOpt)) {
            System.err.println("No event found");
            return;
        }

        Event event = eventOpt.get();

        System.out.println("> Select air dates: ");
        LocalDateTime airDate = selectAirDate(event.getAirDates());
        System.out.println("> Select seats: ");
        Set<Long> seats = null;
        try {
            seats = selectSeats(event, airDate);
        } catch (ApplicationException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("> Select user: ");

        Optional<User> userOpt = selectDomainObject(userService, u -> u.getFirstName() + " " + u.getLastName());
        if (userOpt == null || !userOpt.isPresent()) {
            System.out.println("No user found");
        }

        User user = userOpt.get();

        double price = 0;
        try {
            price = bookingService.getTicketsPrice(event, airDate, user, seats);
        } catch (ApplicationException e) {
            e.printStackTrace();
            return;
        }
        printDelimiter();
        System.out.println("Price for tickets: " + price);
    }

    private Set<Long> selectSeats(Event event, LocalDateTime airDate) throws ApplicationException {
        Long audId = event.getAuditoriumsIds().get(airDate);
        Optional<Auditorium> aud = audService.getAll().stream().filter(a -> a.getId().equals(audId)).findFirst();
        if(!aud.isPresent()){
            return new HashSet<>();
        }

        Set<Ticket> tickets = bookingService.getPurchasedTicketsForEvent(event, airDate);
        List<Long> bookedSeats = tickets.stream().map(t -> t.getSeat()).collect(Collectors.toList());
        List<Long> freeSeats = aud.get().getAllSeats().stream().filter(seat -> !bookedSeats.contains(seat))
                .collect(Collectors.toList());

        System.out.println("Free seats: ");
        System.out.println(freeSeats);

        return inputSeats();
    }

    private Set<Long> inputSeats() {
        Set<Long> set = readInput("Input seats (comma separated): ", s ->
                Arrays.stream(s.split(","))
                        .map(String::trim)
                        .mapToLong(Long::parseLong)
                        .boxed().collect(Collectors.toSet()));
        return set;
    }

    private LocalDateTime selectAirDate(Set<LocalDateTime> airDates) {
        List<LocalDateTime> list = airDates.stream().collect(Collectors.toList());
        for (int i = 0; i < list.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + formatDateTime(list.get(i)));
        }
        int dateIndex = readIntInput("Input air date index: ", list.size()) - 1;
        return list.get(dateIndex);
    }

    private <T extends DomainObject> Optional<T> selectDomainObject(AbstractDomainObjectService<T> service, Function<T, String> displayFunction) {
        if (!service.getAll().isEmpty()) {
            service.getAll().forEach(obj -> System.out.println("[" + obj.getId() + "] " + displayFunction.apply(obj)));
            long id = readIntInput("Input id (-1 for nothing): ");
            return service.getById(id);
        } else {
            return null;
        }
    }

}

