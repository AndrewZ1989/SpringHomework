package repositories;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.primitives.Longs;
import domainModel.User;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class UsersRepositoryDbImpl extends DbRepositoryImpl<User> implements UsersRepository {

    private volatile AtomicLong auditoriumsCount = new AtomicLong(0);

    public UsersRepositoryDbImpl(DataSource source) {
        super(source);
        auditoriumsCount.set(getMaxId());
    }

    @Override
    public Collection<User> getAll() {
        List<User> all = template.query("SELECT id,firstName,lastName,email,birthDate,tickets FROM Users",
                resultSet -> {
                    List<User> data = new ArrayList<>();

                    while (resultSet.next()){
                        Long id = resultSet.getLong("id");
                        String firstName = resultSet.getString("firstName");
                        String lastName = resultSet.getString("lastName");
                        String email = resultSet.getString("email");
                        Date birthDate = resultSet.getDate("birthDate");
                        String tickets = resultSet.getString("tickets");

                        HashSet<Long> ticketsIds = new HashSet<>();
                        for(String ticketId : Splitter.on(",").split(tickets)){
                            Long l = Longs.tryParse(ticketId);
                            if( l != null){
                                ticketsIds.add(l);
                            }
                        }

                        User a = new User(id, null);
                        a.setFirstName(firstName);
                        a.setLastName(lastName);
                        a.setEmail(email);

                        data.add(a);
                    }

                    return data;
                });
        return all;
    }

    @Override
    public void remove(User e) {
        String sql = String.format("DELETE FROM Users WHERE id = %i",e.getId());
        template.execute(sql);
    }

    @Override
    public void save(User e) {
        Integer cnt = template.queryForObject(
                "SELECT count(*) FROM Users WHERE id = ?", Integer.class, e.getId());
        boolean exists = cnt != null && cnt > 0;

        List<String> ticketsIds = e.getTickets().stream().map(t -> t.getId().toString()).collect(Collectors.toList());
        String ticketsIdsString = Joiner.on(',').join(ticketsIds);

        Date birthDateToSave = java.sql.Timestamp.valueOf(e.getBirthDate());

        if(exists){
            String updateSql = "UPDATE Users SET firstName = ?, lastName = ?, email = ?, birthDate = ?, tickets = ? WHERE id = ?";
            template.update(updateSql, new Object[]{e.getFirstName(), e.getLastName(), e.getEmail(), birthDateToSave, ticketsIdsString, e.getId()});
        }else {
            String insertSql = "INSERT INTO Users (id,firstName,lastName,email,birthDate,tickets) VALUES (?,?,?,?,?,?)";
            template.update(insertSql, new Object[]{e.getId(), e.getFirstName(), e.getLastName(), e.getEmail(), birthDateToSave, ticketsIdsString});
        }
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
    public User createNew(LocalDate birthDate) {
        return new User(auditoriumsCount.addAndGet(1), birthDate);
    }

    private long getMaxId() {
        String sql = "SELECT MAX(id) as id FROM Users";
        return  template.query(sql, resultSet -> {
            resultSet.next();
            return resultSet.getLong("id");
        });
    }
}
