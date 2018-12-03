package domainServices;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import domainModel.User;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 */
public interface UserService extends AbstractDomainObjectService<User> {

    /**
     * Finding user by email
     *
     * @param email
     *            Email of the user
     * @return found user or <code>null</code>
     */
    Optional<User> getUserByEmail(@Nonnull String email);


    User createNew(LocalDateTime birthDate);

}

