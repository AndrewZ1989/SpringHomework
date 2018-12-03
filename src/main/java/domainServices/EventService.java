package domainServices;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import domainModel.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;


public interface EventService extends AbstractDomainObjectService<Event> {

    Event create();

    Optional<Event> getByName(@Nonnull String name);

    Collection<Event> getForDateRange(LocalDateTime from, LocalDateTime to);

}
