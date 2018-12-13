package repositories;

import domainModel.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UsersRepositoryImpl extends RepositoryImpl<User> implements UsersRepository {
    @Override
    public User createNew(LocalDateTime birthDate) {
        long id = objCount.addAndGet(1);
        return new User(id, birthDate);
    }
}
