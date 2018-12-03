package domainServices;

import domainModel.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class UserServiceImpl implements UserService {

    public UserServiceImpl(){
        _storage = new ArrayList<>();
    }

    private ArrayList<User> _storage;
    private static volatile AtomicLong _usersCount = new AtomicLong(0);


    @Nullable
    @Override
    public Optional<User> getUserByEmail(@Nonnull String email)
    {
        return _storage.stream().filter( x -> x.getEmail().equals(email)).findFirst();
    }

    @Override
    public User createNew(LocalDateTime birthDate) {
        long id = _usersCount.addAndGet(1);
        return new User(id, birthDate);
    }

    @Override
    public void save(@Nonnull User object)
    {
        if(_storage.contains(object)){
            _storage.remove(object);
        }
        _storage.add(object);
    }

    @Override
    public void remove(@Nonnull User object) {
        _storage.remove(object);
    }

    @Override
    public Optional<User> getById(@Nonnull Long id)
    {
        return _storage.stream().filter( x -> x.getId().equals(id)).findFirst();
    }

    @Nonnull
    @Override
    public Collection<User> getAll() {

        return _storage;
    }
}
