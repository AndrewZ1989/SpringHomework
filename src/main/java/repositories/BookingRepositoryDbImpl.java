package repositories;

import domainModel.Event;
import domainModel.Ticket;
import domainModel.User;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class BookingRepositoryDbImpl extends DbRepositoryImpl<Ticket> implements BookingRepository {

    private volatile AtomicLong count = new AtomicLong(0);
    private UsersRepository uRep;
    private EventsRepository eRep;

    public BookingRepositoryDbImpl(DataSource source,
                                   UsersRepository uRep,
                                   EventsRepository eRep) {
        super(source);
        this.uRep = uRep;
        this.eRep = eRep;
        count.set(getMaxId());
    }

    @Override
    public Ticket createTicket(User user, Event event, LocalDateTime dateTime, long seat) {
        return new Ticket(count.addAndGet(1), user, event, dateTime, seat);
    }

    @Override
    public Collection<Ticket> getAll() {
        List<Ticket> all = template.query("SELECT id, userId, eventId, dateTime, seat FROM Tickets",
                resultSet -> {
                    List<Ticket> data = new ArrayList<>();

                    while (resultSet.next()){
                        Long id = resultSet.getLong("id");
                        Long userId = resultSet.getLong("userId");
                        Long eventId = resultSet.getLong("eventId");
                        Timestamp dateTime = resultSet.getTimestamp("dateTime");
                        Long seat = resultSet.getLong("seat");

                        Optional<User> usr = uRep.tryGetFirst(u -> u.getId().equals(userId));
                        if(!usr.isPresent()){
                            break;
                        }

                        Optional<Event> evt = eRep.tryGetFirst(e -> e.getId().equals(eventId));
                        if(!evt.isPresent()){
                            break;
                        }

                        Ticket t = new Ticket(id, usr.get(), evt.get(), dateTime.toLocalDateTime(), seat);
                        data.add(t);
                    }

                    return data;
                });
        return all;
    }

    @Override
    public void remove(Ticket e) {
        String sql = String.format("DELETE FROM Tickets WHERE id = %i",e.getId());
        template.execute(sql);
    }

    @Override
    public void save(Ticket e) {
        Integer cnt = template.queryForObject(
                "SELECT count(*) FROM Tickets WHERE id = ?", Integer.class, e.getId());
        boolean exists = cnt != null && cnt > 0;

        java.sql.Timestamp dateToSave = java.sql.Timestamp.valueOf(e.getDateTime());

        if(exists){
            String updateSql = "UPDATE Tickets SET userId = ?, eventId = ?, dateTime = ?, seat = ? WHERE id = ?";
            template.update(updateSql, new Object[]{e.getUser().getId(), e.getEvent().getId(), dateToSave, e.getSeat(), e.getId()});
        }else {
            String updateSql = "INSERT INTO Tickets (id, userId, eventId, dateTime, seat) VALUES(?,?,?,?,?)";
            template.update(updateSql, new Object[]{ e.getId(), e.getUser().getId(), e.getEvent().getId(), dateToSave, e.getSeat()});
        }
    }

    @Override
    protected List<String> getCreateTableSqlStatements() {
        return Collections.singletonList("CREATE TABLE Tickets (" +
                "    id INTEGER NOT NULL PRIMARY KEY," +
                "    userId INTEGER NOT NULL," +
                "    eventId INTEGER NOT NULL," +
                "    dateTime TIMESTAMP NOT NULL," +
                "    seat INTEGER NOT NULL)");
    }


    private long getMaxId() {
        String sql = "SELECT MAX(id) as id FROM Tickets";
        return  template.query(sql, resultSet -> {
            resultSet.next();
            return resultSet.getLong("id");
        });
    }


}
