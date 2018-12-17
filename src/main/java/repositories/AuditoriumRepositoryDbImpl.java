package repositories;

import domainModel.Auditorium;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class AuditoriumRepositoryDbImpl extends DbRepositoryImpl<Auditorium> implements AuditoriumRepository {

    @Autowired
    public AuditoriumRepositoryDbImpl(@Qualifier("dataSource")DataSource source) throws SQLException {
        super(source);
    }

    @Override
    public Auditorium create() {

        return null;
    }

    @Override
    public Collection<Auditorium> getAll() {
        return null;
    }

    @Override
    public void remove(Auditorium e) {

    }

    @Override
    public void save(Auditorium e) {

    }

    @Override
    protected String getCreateTableSqlStatement() {
        return "CREATE TABLE Auditoriums (" +
                "    id INTEGER NOT NULL PRIMARY KEY," +
                "    name VARCHAR(45) NOT NULL," +
                "    number_of_seats INTEGER NOT NULL )";
    }
}
