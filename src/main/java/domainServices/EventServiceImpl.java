package domainServices;

import domainModel.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import repositories.EventsRepository;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EventServiceImpl implements EventService {

    private EventsRepository rep;

    @Autowired
    public EventServiceImpl(EventsRepository rep){

        this.rep = rep;
    }

    @Override
    public Event create() {
        return rep.create();
    }

    @Override
    public Optional<Event> getByName(@Nonnull String name) {
       return rep.tryGetFirst(x -> x.getName().equals(name));
    }

    @Override
    public Collection<Event> getForDateRange(LocalDateTime from, LocalDateTime to) {
        return rep.filter(x -> thereIsDateInRange(x, from, to));
    }

    @Override
    public void save(@Nonnull Event object) {
        rep.save(object);
    }

    @Override
    public void remove(@Nonnull Event object) {
        rep.remove(object);
    }

    @Override
    public Optional<Event> getById(@Nonnull Long id) {
        return rep.tryGetFirst(x -> x.getId().equals(id));
    }

    @Nonnull
    @Override
    public Collection<Event> getAll() {
        return rep.getAll();
    }

    private boolean thereIsDateInRange(Event e, LocalDateTime from, LocalDateTime to){
        for (LocalDateTime d : e.getAirDates()){
            if( d.isAfter(from) && d.isBefore(to)){
                return true;
            }
        }
        return false;
    }
}
