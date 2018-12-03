package repositories;

import domainModel.Event;
import domainModel.Ticket;
import domainModel.User;

import java.time.LocalDateTime;

public class BookingRepositoryImpl extends RepositoryImpl<Ticket> implements BookingRepository {
    @Override
    public Ticket createTicket(User user, Event event, LocalDateTime dateTime, long seat) {
        long id = _objCount.addAndGet(1);
        return new Ticket(id, user, event, dateTime, seat);
    }
}