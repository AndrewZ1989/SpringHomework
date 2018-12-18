package repositories;

import domainModel.Event;
import domainModel.Ticket;
import domainModel.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class BookingRepositoryImpl extends RepositoryImpl<Ticket> implements BookingRepository {
    @Override
    public Ticket createTicket(User user, Event event, LocalDateTime dateTime, long seat) {
        long id = objCount.addAndGet(1);
        return new Ticket(id, user, event, dateTime, seat);
    }
}
