package aspectsRepositories;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import utility.Tuple;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

public class DiscountAspectRepositoryDb implements DiscountAspectRepository {

    protected JdbcTemplate template;

    public DiscountAspectRepositoryDb(DataSource ds) {
        template = new JdbcTemplate(ds);
        initDatabase();
    }


    @Override
    public Map<String, HashMap<Long, Integer>> getAll() {
        Map<String, HashMap<Long, Integer>> ans  = new HashMap<>();

        List<Tuple<String, Tuple<Long, Integer>>> all = template.query(
                "SELECT strategyName, userId, count FROM DiscountAspectData",
                resultSet -> {
                    List<Tuple<String, Tuple<Long, Integer>>> list = new ArrayList<>();
                    while (resultSet.next()){
                        String strategyName = resultSet.getString("strategyName");
                        Long userId = resultSet.getLong("userId");
                        int count = resultSet.getInt("count");

                        list.add(new Tuple<>(strategyName, new Tuple<>(userId, count)));
                    }
                    return list;
                });

        for(Tuple<String, Tuple<Long, Integer>> x : all){
            if(!ans.containsKey(x.first)){
                ans.put(x.first, new HashMap<>());
            }

            HashMap<Long, Integer> data = ans.get(x.first);
            data.put(x.second.first, x.second.second);
        }
        return ans;
    }

    @Override
    public Optional<HashMap<Long, Integer>> getFor(String strategyClassName) {
        HashMap<Long, Integer> res = getAll().get(strategyClassName);
        if(res == null){
            return Optional.empty();
        }
        return Optional.of(res);
    }

    @Override
    public void save(String strategyName, Long userId, int count) {
        boolean exists = checkIfExists(strategyName, userId);

        if(exists){
            String updateSql = "UPDATE DiscountAspectData SET count = ? WHERE strategyName = ? AND userId = ?";
            template.update(updateSql, new Object[]{count, strategyName, userId});
        }else {
            String insertSql = "INSERT INTO DiscountAspectData (strategyName,userId,count) VALUES (?,?,?)";
            template.update(insertSql, new Object[]{strategyName, userId, count});
        }
    }


    private void initDatabase() {
        String sql = "CREATE TABLE DiscountAspectData (" +
                "    strategyName VARCHAR(500) NOT NULL," +
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

    private boolean checkIfExists(String strategyName, Long userId){
        Integer cnt = template.queryForObject(
                "SELECT count(*) FROM DiscountAspectData WHERE strategyName = ? AND userId = ?", Integer.class, strategyName, userId);
        return cnt != null && cnt > 0;
    }
}
