package domainServices;

import domainModel.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class UserServiceImpl implements UserService {
    @Nullable
    @Override
    public User getUserByEmail(@Nonnull String email) {
        return null;
    }

    @Override
    public User save(@Nonnull User object) {
        return null;
    }

    @Override
    public void remove(@Nonnull User object) {

    }

    @Override
    public User getById(@Nonnull Long id) {
        return null;
    }

    @Nonnull
    @Override
    public Collection<User> getAll() {
        return null;
    }
}
