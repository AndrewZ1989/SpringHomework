package repositories;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.primitives.Longs;
import domainModel.Auditorium;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class AuditoriumRepositoryDbImpl extends DbRepositoryImpl<Auditorium> implements AuditoriumRepository {

    private volatile AtomicLong auditoriumsCount = new AtomicLong(0);

    @Autowired
    public AuditoriumRepositoryDbImpl(@Qualifier("dataSource")DataSource source) throws SQLException {
        super(source);
        auditoriumsCount.set(getMaxId());
    }

    @Override
    public Auditorium create() {
        return new Auditorium(auditoriumsCount.addAndGet(1));
    }

    @Override
    public Collection<Auditorium> getAll() {
        List<Auditorium> all = template.query("SELECT id, name, number_of_seats, vip_seats FROM Auditoriums", new ResultSetExtractor<List<Auditorium>>() {
            @Override
            public List<Auditorium> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                List<Auditorium> data = new ArrayList<>();

                while (resultSet.next()){
                    Long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    int number_of_seats = resultSet.getInt("number_of_seats");
                    String vip_seats = resultSet.getString("vip_seats");

                    HashSet<Long> vipSeats = new HashSet<>();
                    for(String vipSeat : Splitter.on(",").split(vip_seats)){
                        Long l = Longs.tryParse(vipSeat);
                        if( l != null){
                            vipSeats.add(l);
                        }
                    }

                    Auditorium a = new Auditorium(id);
                    a.setVipSeats(vipSeats);
                    a.setNumberOfSeats(number_of_seats);
                    a.setName(name);

                    data.add(a);
                }

                return data;
            }
        });
        return all;
    }

    @Override
    public void remove(Auditorium e) {
        String sql = String.format("DELETE FROM Auditoriums WHERE id = %i",e.getId());
        template.execute(sql);
    }

    @Override
    public void save(Auditorium e) {
        String checkIfExistsSql = "SELECT * FROM Auditoriums WHERE id = ?";

        Integer cnt = template.queryForObject(
                "SELECT count(*) FROM Auditoriums WHERE id = ?", Integer.class, e.getId());
        boolean exists = cnt != null && cnt > 0;

        String vipSeatsString = Joiner.on(',').join(e.getVipSeats());

        if(exists){
            String updateSql = "UPDATE Auditoriums SET name = ?, number_of_seats = ?, vip_seats = ? WHERE id = ?";
            template.update(updateSql, new Object[]{e.getName(), e.getNumberOfSeats(), vipSeatsString, e.getId()});
        }else {
            String insertSql = "INSERT INTO Auditoriums (id,name,number_of_seats,vip_seats) VALUES (?,?,?,?)";
            template.update(insertSql, new Object[]{e.getId(), e.getName(), e.getNumberOfSeats(), vipSeatsString});
        }
    }

    @Override
    protected String getCreateTableSqlStatement() {
        return "CREATE TABLE Auditoriums (" +
                "    id INTEGER NOT NULL PRIMARY KEY," +
                "    name VARCHAR(45) NOT NULL," +
                "    number_of_seats INTEGER NOT NULL," +
                "    vip_seats VARCHAR(500))";
    }


    private long getMaxId() {
        String sql = "SELECT MAX(id) as id FROM Auditoriums";
        return  template.query(sql, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                resultSet.next();
                return resultSet.getLong("id");
            }
        });
    }
}
