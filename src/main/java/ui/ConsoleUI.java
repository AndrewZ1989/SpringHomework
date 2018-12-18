package ui;

import java.time.LocalDateTime;
import java.util.Collections;

import aspects.DiscountAspect;
import org.springframework.context.ApplicationContext;

import domainModel.Auditorium;
import domainModel.Event;
import domainModel.EventRating;
import domainModel.Ticket;
import domainModel.User;

import domainServices.AuditoriumService;
import domainServices.BookingService;
import domainServices.EventService;
import domainServices.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import springConfig.AppConfig;
import ui.state.InitialMenu;

/**
 */
public class ConsoleUI {

    private AnnotationConfigApplicationContext context;

    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        ui.initContext();
        ui.run();
    }

    private void initContext() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.scan("aspects", "repositories", "domainServices", "aspectsRepositories");
    }

    private void run() {
        System.out.println("Welcome to movie theater console service");

        fillInitialData();

        InitialMenu state = new InitialMenu(context);

        state.run();

        System.out.println("Exiting.. Thank you.");
    }

    private void fillInitialData() {
        UserService userService = context.getBean(UserService.class);
        EventService eventService = context.getBean(EventService.class);
        AuditoriumService auditoriumService = context.getBean(AuditoriumService.class);
        BookingService bookingService = context.getBean(BookingService.class);

        Auditorium newAuditorium = auditoriumService.create();
        newAuditorium.setName("Test");
        newAuditorium.setNumberOfSeats(100);

        auditoriumService.add(newAuditorium);

        Auditorium auditorium = auditoriumService.getAll().iterator().next();
        if (auditorium == null) {
            throw new IllegalStateException("Failed to fill initial data - no auditoriums returned from AuditoriumService");
        }
        if (auditorium.getNumberOfSeats() <= 0) {
            throw new IllegalStateException("Failed to fill initial data - no seats in the auditorium " + auditorium.getName());
        }

        User user = userService.createNew(LocalDateTime.of(2000, 10,1,11,00));
        user.setEmail("my@email.com");
        user.setFirstName("Foo");
        user.setLastName("Bar");

        userService.save(user);

        Event event = eventService.create();
        event.setName("Grand concert");
        event.setRating(EventRating.MID);
        event.setBasePrice(10);
        LocalDateTime airDate = LocalDateTime.of(2020, 6, 15, 19, 30);
        event.addAirDateTime(airDate, auditorium);

        eventService.save(event);

        Ticket ticket1 = bookingService.createTicket(user, event, airDate, 1);
        bookingService.bookTickets(Collections.singleton(ticket1));

        if (auditorium.getNumberOfSeats() > 1) {
            User userNotRegistered = userService.createNew(LocalDateTime.of(2009,6,3,14,1));
            userNotRegistered.setEmail("somebody@a.b");
            userNotRegistered.setFirstName("A");
            userNotRegistered.setLastName("Somebody");
            Ticket ticket2 = bookingService.createTicket(userNotRegistered, event, airDate, 2);
            bookingService.bookTickets(Collections.singleton(ticket2));
        }
    }
}

