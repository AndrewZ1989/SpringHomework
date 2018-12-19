package repositories;

import domainModel.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Collection;

public class UsersRepositoryDbImpl extends DbRepositoryImpl<User> implements UsersRepository {


    public UsersRepositoryDbImpl(DataSource source) {
        super(source);
    }

    @Override
    public Collection<User> getAll() {
        throw new NotImplementedException();
    }

    @Override
    public void remove(User e) {
        throw new NotImplementedException();
    }

    @Override
    public void save(User e) {
        throw new NotImplementedException();
    }

    @Override
    protected String getCreateTableSqlStatement() {
        return "CREATE TABLE Users (" +
                "    id INTEGER NOT NULL PRIMARY KEY," +
                "    firstName VARCHAR(45) NOT NULL," +
                "    lastName VARCHAR(45) NOT NULL," +
                "    email VARCHAR(45) NOT NULL," +
                "    birthDate DATE NOT NULL," +
                "    tickets VARCHAR(500))";
    }

    @Override
    public User createNew(LocalDateTime birthDate) {
        throw new NotImplementedException();
    }
}
