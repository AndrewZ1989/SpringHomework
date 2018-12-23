package repositories;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import domainModel.Event;
import domainModel.EventRating;
import exceptions.ApplicationException;
import utility.Tuple;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EventsRepositoryDbImpl extends DbRepositoryImpl<Event> implements EventsRepository {

    private volatile AtomicLong count = new AtomicLong(0);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS");

    public EventsRepositoryDbImpl(DataSource source) {
        super(source);
        count.set(getMaxId());
    }

    @Override
    public Collection<Event> getAll() {

        List<Event> all = template.query("SELECT id,name,airDates,basePrice,rating FROM Events",
                resultSet -> {
                    List<Event> data = new ArrayList<>();

                    while (resultSet.next()) {
                        Long id = resultSet.getLong("id");
                        String name = resultSet.getString("name");
                        String airDates = resultSet.getString("airDates");
                        Double basePrice = resultSet.getDouble("basePrice");
                        Integer rating = resultSet.getInt("rating");

                        HashSet<LocalDateTime> airDatesForEvent = new HashSet<>();
                        for(String airDate : Splitter.on(",").split(airDates)){
                            if(airDate != null){
                                LocalDateTime date = LocalDateTime.parse(airDate, formatter);
                                if( date != null){
                                    airDatesForEvent.add(date);
                                }
                            }
                        }

                        Event event = new Event(id);
                        event.setName(name);
                        event.setAirDates(airDatesForEvent);
                        event.setBasePrice(basePrice);
                        event.setRating(EventRating.values()[rating]);

                        data.add(event);
                    }
                    return data;
                });

        for(Event e : all){
            List<Tuple<LocalDateTime, Long>> auditoriums = template.query("SELECT dateTime,auditoriumId FROM EventsAuditoriums WHERE eventId = ? ",
                    new Object[]{e.getId()},
                    resultSet ->
                    {
                        List<Tuple<LocalDateTime, Long>> res = new ArrayList<>();

                        while (resultSet.next()) {
                            Timestamp dateTime = resultSet.getTimestamp("dateTime");
                            Long auditoriumId = resultSet.getLong("auditoriumId");

                            res.add(new Tuple<>(dateTime.toLocalDateTime(), auditoriumId));
                        }

                        return res;
                    });

            Map<LocalDateTime, Long> auditoriumsForEvent = new HashMap<>();
            for(Tuple<LocalDateTime,Long> x : auditoriums){
                auditoriumsForEvent.put(x.first, x.second);
            }

            e.setAuditoriumsIds(auditoriumsForEvent);
        }

        return all;
    }

    @Override
    public void remove(Event e) {
        String sql = String.format("DELETE FROM Events WHERE id = %i",e.getId());
        template.execute(sql);
    }

    @Override
    public void save(Event e) throws ApplicationException {
        saveEvent(e);
        saveAuditoriums(e);
    }

    private void saveAuditoriums(Event e) {
        for(Map.Entry<LocalDateTime,Long> item : e.getAuditoriumsIds().entrySet()){
            Integer cnt = template.queryForObject(
                    "SELECT count(*) FROM EventsAuditoriums WHERE eventId = ? AND auditoriumId = ?", Integer.class, e.getId(), item.getValue());
            boolean exists = cnt != null && cnt > 0;

            java.sql.Timestamp dateToSave = java.sql.Timestamp.valueOf(item.getKey());

            if(!exists){
                String insertSql = "INSERT INTO EventsAuditoriums (eventId,dateTime,auditoriumId) VALUES (?,?,?)";
                template.update(insertSql, new Object[]{e.getId(), dateToSave, item.getValue()});
            }else {
                String updateSql = "UPDATE EventsAuditoriums SET dateTime = ? WHERE eventId = ? AND auditoriumId = ?";
                template.update(updateSql, new Object[]{dateToSave, e.getId(), item.getValue()});
            }

        }
    }

    private void saveEvent(Event e) {
        Integer cnt = template.queryForObject(
                "SELECT count(*) FROM Events WHERE id = ?", Integer.class, e.getId());
        boolean exists = cnt != null && cnt > 0;

        String airDates = Joiner.on(',').join( e.getAirDates().stream().map(d -> d.format(formatter) ).collect(Collectors.toList()) );

        if(exists){
            String updateSql = "UPDATE Events SET name = ?, airDates = ?, basePrice = ?, rating = ? WHERE id = ?";
            template.update(updateSql, new Object[]{e.getName(), airDates, e.getBasePrice(), e.getRating().getValue(), e.getId()});
        }else {
            String insertSql = "INSERT INTO Events (id,name,airDates,basePrice,rating) VALUES (?,?,?,?,?)";
            template.update(insertSql, new Object[]{e.getId(), e.getName(), airDates, e.getBasePrice(), e.getRating().getValue()});
        }
    }

    @Override
    protected List<String> getCreateTableSqlStatements() {
        String createEvents = "CREATE TABLE Events (" +
                "    id INTEGER NOT NULL PRIMARY KEY," +
                "    name VARCHAR(450) NOT NULL," +
                "    airDates VARCHAR(500) NOT NULL," +
                "    basePrice DOUBLE," +
                "    rating INTEGER NOT NULL )";

        String createEventsAuditoriums = "CREATE TABLE EventsAuditoriums(" +
                "eventId INTEGER NOT NULL PRIMARY KEY," +
                "dateTime TIMESTAMP NOT NULL," +
                "auditoriumId INTEGER NOT NULL" +
                ")";

        return Arrays.asList(createEvents, createEventsAuditoriums);

    }

    @Override
    public Event create() {
        return new Event(count.addAndGet(1));
    }



    private long getMaxId() {
        String sql = "SELECT MAX(id) as id FROM Events";
        return  template.query(sql, resultSet -> {
            resultSet.next();
            return resultSet.getLong("id");
        });
    }


}
