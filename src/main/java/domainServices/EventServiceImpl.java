package domainServices;

import domainModel.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EventServiceImpl implements EventService {

    public EventServiceImpl(){
        _storage = new ArrayList<>();
    }

    private ArrayList<Event> _storage;
    private static volatile AtomicLong _usersCount = new AtomicLong(0);


    @Override
    public Event create() {
        long id = _usersCount.addAndGet(1);
        return new Event(id);
    }

    @Override
    public Optional<Event> getByName(@Nonnull String name) {
       return _storage.stream().filter(x -> x.getName().equals(name) ).findFirst();
    }

    @Override
    public Collection<Event> getForDateRange(LocalDateTime from, LocalDateTime to) {

        return _storage.stream().filter( x -> thereIsDateInRange(x, from, to) ).collect(Collectors.toList());
    }

    @Override
    public void save(@Nonnull Event object) {
        if(_storage.contains(object)) {
            _storage.remove(object);
        }
        _storage.add(object);
    }

    @Override
    public void remove(@Nonnull Event object) {
        _storage.remove(object);
    }

    @Override
    public Optional<Event> getById(@Nonnull Long id) {
        return _storage.stream().filter(x -> x.getId().equals(id) ).findFirst();
    }

    @Nonnull
    @Override
    public Collection<Event> getAll() {
        return _storage;
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
