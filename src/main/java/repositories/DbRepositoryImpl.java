package repositories;

import domainModel.DomainObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class DbRepositoryImpl<T extends DomainObject> implements Repository<T> {

    protected JdbcTemplate template;

    public DbRepositoryImpl(DataSource source){        
        template = new JdbcTemplate(source);
        createTableIfNotExists();
    }

    @Override
    public Optional<T> tryGetFirst(Predicate<T> p) {
        //This implementation is inefficient and definitely won't be used on production
        return getAll().stream().filter(p).findFirst();
    }

    @Override
    public Collection<T> filter(Predicate<T> p) {
        //This implementation is inefficient and definitely won't be used on production
        return getAll().stream().filter(p).collect(Collectors.toList());
    }

    @Override
    public abstract Collection<T> getAll();

    @Override
    public abstract void remove(T e);

    @Override
    public abstract void save(T e);



    private void createTableIfNotExists() {
        String sql = getCreateTableSqlStatement();
        try {
            template.execute(sql);
        }catch (DataIntegrityViolationException ex){
            Throwable cause = ex.getCause();
            if(cause != null && cause instanceof SQLException){
                SQLException se = (SQLException)cause;
                if (se.getSQLState().equals("X0Y32")){
                    return;
                }
            }
            throw ex;
        }
    }

    protected abstract String getCreateTableSqlStatement();
}
