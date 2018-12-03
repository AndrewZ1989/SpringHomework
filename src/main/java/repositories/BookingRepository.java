package repositories;

import domainModel.Event;
import domainModel.Ticket;
import domainModel.User;

import java.time.LocalDateTime;

public interface BookingRepository extends Repository<Ticket> {
    Ticket createTicket(User user, Event event, LocalDateTime dateTime, long seat);
}
