package repositories;

import domainModel.DomainObject;
import domainModel.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class RepositoryImpl<T extends DomainObject> implements Repository<T> {

    public RepositoryImpl(){
        _storage = new ArrayList<>();
    }

    protected ArrayList<T> _storage;
    protected volatile AtomicLong _objCount = new AtomicLong(0);

    @Override
    public Optional<T> tryGetFirst(Predicate<T> p) {
        return _storage.stream().filter(p).findFirst();
    }

    @Override
    public Collection<T> filter(Predicate<T> p) {
        return _storage.stream().filter(p).collect(Collectors.toList());
    }

    @Override
    public Collection<T> getAll() {
        return _storage;
    }

    @Override
    public void remove(T e) {
        _storage.remove(e);
    }

    @Override
    public void save(T e) {
        if(_storage.contains(e)) {
            _storage.remove(e);
        }
        _storage.add(e);
    }
}
