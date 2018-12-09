package domainServices;

import domainModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import repositories.UsersRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UserServiceImpl implements UserService {

    private UsersRepository rep;

    @Autowired
    public UserServiceImpl(UsersRepository rep)
    {

        this.rep = rep;
    }


    @Nullable
    @Override
    public Optional<User> getUserByEmail(@Nonnull String email)
    {
        return rep.tryGetFirst(x -> x.getEmail().equals(email));
    }

    @Override
    public User createNew(LocalDateTime birthDate) {
        return rep.createNew(birthDate);
    }

    @Override
    public void save(@Nonnull User object)
    {
        rep.save(object);
    }

    @Override
    public void remove(@Nonnull User object) {
        rep.remove(object);
    }

    @Override
    public Optional<User> getById(@Nonnull Long id)
    {
        return rep.tryGetFirst(x -> x.getId().equals(id));
    }

    @Nonnull
    @Override
    public Collection<User> getAll() {
        return rep.getAll();
    }
}
