package aspectsRepositories;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface DiscountAspectRepository {

    Map<String, HashMap<Long, Integer>> getAll();

    Optional<HashMap<Long, Integer>> getFor(String strategyClassName);

    void save(String strategyName, Long userId, int count);
}
