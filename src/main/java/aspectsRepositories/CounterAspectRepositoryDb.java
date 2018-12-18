package aspectsRepositories;

import aspects.EventStatistics;
import domainModel.Event;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import utility.Tuple;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CounterAspectRepositoryDb implements CounterAspectRepository {

    protected JdbcTemplate template;

    public CounterAspectRepositoryDb(DataSource source){
        template = new JdbcTemplate(source);
        initDatabase();
    }

    @Override
    public Map<Long, EventStatistics> getAll() {
        List<Tuple<Long, EventStatistics>> all = template.query("SELECT eventId, byNameAccessCount, priceQueriedCount, bookedTicketsCount FROM CounterAspectData",
                resultSet -> {
                    List<Tuple<Long, EventStatistics>> res = new ArrayList<>();
                    while (resultSet.next()){
                        Long id = resultSet.getLong("eventId");
                        int byNameAccessCount = resultSet.getInt("byNameAccessCount");
                        int priceQueriedCount = resultSet.getInt("priceQueriedCount");
                        int bookedTicketsCount = resultSet.getInt("bookedTicketsCount");

                        EventStatistics st = new EventStatistics();
                        st.setByNameAccessCount(byNameAccessCount);
                        st.setPriceQueriedCount(priceQueriedCount);
                        st.setBookedTicketsCount(bookedTicketsCount);

                        res.add(new Tuple<>(id, st));
                    }
                    return res;
                });

        Map<Long, EventStatistics> ans = new HashMap<>();
        for(Tuple<Long, EventStatistics> x : all){
            ans.put(x.first, x.second);
        }
        return ans;
    }

    @Override
    public boolean hasDataFor(Event e) {
        return checkIfExists(e);
    }

    @Override
    public EventStatistics getFor(Event e) {
        boolean exists = checkIfExists(e);
        if(exists){
            return  getAll().get(e.getId());
        }
        return null;
    }

    @Override
    public void save(Event e, EventStatistics stats) {
        boolean exists = checkIfExists(e);

        if(exists){
            String updateSql = "UPDATE CounterAspectData SET byNameAccessCount = ?, priceQueriedCount = ?, bookedTicketsCount = ? WHERE eventId = ?";
            template.update(updateSql, new Object[]{stats.getByNameAccessCount(), stats.getPriceQueriedCount(), stats.getBookedTicketsCount(), e.getId()});
        }else {
            String insertSql = "INSERT INTO CounterAspectData (eventId,byNameAccessCount,priceQueriedCount,bookedTicketsCount) VALUES (?,?,?,?)";
            template.update(insertSql, new Object[]{e.getId(), stats.getByNameAccessCount(), stats.getPriceQueriedCount(), stats.getBookedTicketsCount()});
        }
    }


    private void initDatabase() {
        String sql = "CREATE TABLE CounterAspectData (" +
                "    eventId INTEGER NOT NULL," +
                "    byNameAccessCount INTEGER NOT NULL," +
                "    priceQueriedCount INTEGER NOT NULL," +
                "    bookedTicketsCount INTEGER NOT NULL )";
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

    private boolean checkIfExists(Event e){
        Integer cnt = template.queryForObject(
                "SELECT count(*) FROM CounterAspectData WHERE eventId = ?", Integer.class, e.getId());
        return cnt != null && cnt > 0;
    }

}
