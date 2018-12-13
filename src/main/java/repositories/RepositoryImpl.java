package repositories;

import domainModel.DomainObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class RepositoryImpl<T extends DomainObject> implements Repository<T> {

    public RepositoryImpl(){
        storage = new ArrayList<>();
    }

    protected ArrayList<T> storage;
    protected volatile AtomicLong objCount = new AtomicLong(0);

    @Override
    public Optional<T> tryGetFirst(Predicate<T> p) {
        return storage.stream().filter(p).findFirst();
    }

    @Override
    public Collection<T> filter(Predicate<T> p) {
        return storage.stream().filter(p).collect(Collectors.toList());
    }

    @Override
    public Collection<T> getAll() {
        return storage;
    }

    @Override
    public void remove(T e) {
        storage.remove(e);
    }

    @Override
    public void save(T e) {
        if(storage.contains(e)) {
            storage.remove(e);
        }
        storage.add(e);
    }
}
