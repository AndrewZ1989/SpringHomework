package repositories;

import domainModel.User;

import java.time.LocalDateTime;

public interface UsersRepository extends Repository<User> {
    User createNew(LocalDateTime birthDate);
}
