package repositories;

import domainModel.User;

import java.time.LocalDateTime;

public class UsersRepositoryImpl extends RepositoryImpl<User> implements UsersRepository {
    @Override
    public User createNew(LocalDateTime birthDate) {
        long id = _objCount.addAndGet(1);
        return new User(id, birthDate);
    }
}
