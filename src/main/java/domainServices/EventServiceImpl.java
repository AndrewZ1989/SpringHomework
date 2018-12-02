package domainServices;

import domainModel.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class EventServiceImpl implements EventService {
    @Nullable
    @Override
    public Event getByName(@Nonnull String name) {
        return null;
    }

    @Override
    public Event save(@Nonnull Event object) {
        return null;
    }

    @Override
    public void remove(@Nonnull Event object) {

    }

    @Override
    public Event getById(@Nonnull Long id) {
        return null;
    }

    @Nonnull
    @Override
    public Collection<Event> getAll() {
        return null;
    }
}
