package domainServices;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import domainModel.Auditorium;
import org.springframework.util.StringUtils;


/**
 */
public interface AuditoriumService {

    /**
     *
     * @return
     */
    Auditorium create();

    /**
     *
     * @param auditorium
     */
    void add(Auditorium auditorium);

    /**
     * Getting all auditoriums from the system
     *
     * @return set of all auditoriums
     */
    @Nonnull
    Collection<Auditorium> getAll();

    /**
     * Finding auditorium by name
     *
     * @param name
     *            Name of the auditorium
     * @return found auditorium or <code>null</code>
     */
    @Nullable Auditorium getByName(@Nonnull String name);

}

