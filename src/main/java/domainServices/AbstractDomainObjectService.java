package domainServices;

import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import domainModel.DomainObject;


/**
 * @param <T>
 *            DomainObject subclass
 */
public interface AbstractDomainObjectService<T extends DomainObject> {


    /**
     * Saving new object to storage or updating existing one
     *
     * @param object
     *            Object to save
     * @return saved object with assigned id
     */
    void save(@Nonnull T object);

    /**
     * Removing object from storage
     *
     * @param object
     *            Object to remove
     */
    void remove(@Nonnull T object);

    /**
     * Getting object by id from storage
     *
     * @param id
     *            id of the object
     * @return Found object or <code>null</code>
     */
    Optional<T> getById(@Nonnull Long id);

    /**
     * Getting all objects from storage
     *
     * @return collection of objects
     */
    @Nonnull Collection<T> getAll();
}

