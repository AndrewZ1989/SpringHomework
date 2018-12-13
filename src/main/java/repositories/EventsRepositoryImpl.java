package repositories;

import domainModel.Event;
import org.springframework.stereotype.Component;

@Component
public class EventsRepositoryImpl extends RepositoryImpl<Event> implements EventsRepository {

    @Override
    public Event create() {
        long id = objCount.addAndGet(1);
        return new Event(id);
    }

}
