package repositories;

import domainModel.Event;

public interface EventsRepository extends Repository<Event> {

    Event create();

}
