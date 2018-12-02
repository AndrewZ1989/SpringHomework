package domainServices;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import domainModel.Auditorium;



/**
 */
public interface AuditoriumService {

    public void add(Auditorium auditorium);

    /**
     * Getting all auditoriums from the system
     *
     * @return set of all auditoriums
     */
    public @Nonnull
    Collection<Auditorium> getAll();

    /**
     * Finding auditorium by name
     *
     * @param name
     *            Name of the auditorium
     * @return found auditorium or <code>null</code>
     */
    public @Nullable Auditorium getByName(@Nonnull String name);

}

