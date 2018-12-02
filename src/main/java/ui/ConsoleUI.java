package ui;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

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
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import ui.state.InitialState;

/**
 */
public class ConsoleUI {

    private ApplicationContext context;

    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        ui.initContext();
        ui.run();
    }

    private void initContext() {
        context = new FileSystemXmlApplicationContext("src/main/resources/springConfig.xml");
    }

    private void run() {
        System.out.println("Welcome to movie theater console service");

        fillInitialData();

        InitialState state = new InitialState(context);

        state.run();

        System.out.println("Exiting.. Thank you.");
    }

    private void fillInitialData() {
        UserService userService = (UserService) context.getBean(UserService.class);
        EventService eventService = context.getBean(EventService.class);
        AuditoriumService auditoriumService = context.getBean(AuditoriumService.class);
        BookingService bookingService = context.getBean(BookingService.class);

        Auditorium newAuditorium = new Auditorium();
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

        User user = new User();
        user.setEmail("my@email.com");
        user.setFirstName("Foo");
        user.setLastName("Bar");

        user = userService.save(user);

        Event event = new Event();
        event.setName("Grand concert");
        event.setRating(EventRating.MID);
        event.setBasePrice(10);
        LocalDateTime airDate = LocalDateTime.of(2020, 6, 15, 19, 30);
        event.addAirDateTime(airDate, auditorium);

        event = eventService.save(event);

        Ticket ticket1 = new Ticket(user, event, airDate, 1);
        bookingService.bookTickets(Collections.singleton(ticket1));

        if (auditorium.getNumberOfSeats() > 1) {
            User userNotRegistered = new User();
            userNotRegistered.setEmail("somebody@a.b");
            userNotRegistered.setFirstName("A");
            userNotRegistered.setLastName("Somebody");
            Ticket ticket2 = new Ticket(userNotRegistered, event, airDate, 2);
            bookingService.bookTickets(Collections.singleton(ticket2));
        }
    }
}

