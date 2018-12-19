package aspectsRepositories;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.Tuple;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

public class LuckyWinnerRepositoryDb implements LuckyWinnerRepository {

    protected JdbcTemplate template;

    public LuckyWinnerRepositoryDb(DataSource ds){
        template = new JdbcTemplate(ds);
        initDatabase();
    }


    @Override
    public Map<Long, Integer> getAll() {
        Map<Long, Integer> res = new HashMap<>();

        List<Tuple<Long, Integer>> all = template.query("SELECT userId, count FROM LuckyAspectData",
                resultSet -> {
                    List<Tuple<Long, Integer>> ans = new ArrayList<>();
                    while (resultSet.next()){
                        Long userId = resultSet.getLong("userId");
                        int count = resultSet.getInt("count");

                        ans.add(new Tuple<>(userId, count));
                    }
                    return ans;
                });

        for(Tuple<Long, Integer> x : all){
         res.put(x.first, x.second);
        }

        return res;
    }

    @Override
    public boolean containsFor(Long userId) {
        Integer cnt = template.queryForObject(
                "SELECT count(*) FROM LuckyAspectData WHERE userId = ?", Integer.class, userId);
        return cnt != null && cnt > 0;
    }

    @Override
    public Integer getFor(Long userid) {
        return getAll().get(userid);
    }

    @Override
    public void save(Long userid, int count) {
        boolean exists = containsFor(userid);

        if(exists){
            String updateSql = "UPDATE LuckyAspectData SET count = ? WHERE userId = ?";
            template.update(updateSql, new Object[]{count, userid});
        }else {
            String insertSql = "INSERT INTO LuckyAspectData (userId,count) VALUES (?,?)";
            template.update(insertSql, new Object[]{userid, count});
        }
    }


    private void initDatabase() {
        String sql = "CREATE TABLE LuckyAspectData (" +
                "    userId INTEGER NOT NULL," +
                "    count INTEGER NOT NULL )";
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
}
