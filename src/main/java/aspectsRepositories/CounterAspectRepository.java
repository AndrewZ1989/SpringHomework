package aspectsRepositories;

import aspects.EventStatistics;
import domainModel.Event;

import java.util.Map;

public interface CounterAspectRepository {

    Map<Long, EventStatistics> getAll();

    boolean hasDataFor(Event e);

    EventStatistics getFor(Event e);

    void save(Event e, EventStatistics stats);
}
