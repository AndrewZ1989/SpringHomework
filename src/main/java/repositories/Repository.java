package repositories;

import domainModel.DomainObject;
import exceptions.ApplicationException;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository<T extends DomainObject> {

    Optional<T> tryGetFirst(Predicate<T> p);

    Collection<T> filter(Predicate<T> p);

    Collection<T> getAll();

    void remove(T e);

    void save(T e) throws ApplicationException;
}
