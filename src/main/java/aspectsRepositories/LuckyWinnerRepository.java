package aspectsRepositories;

import java.util.Map;
import java.util.Optional;

public interface LuckyWinnerRepository {

    Map<Long,Integer> getAll();

    boolean containsFor(Long userId);

    Integer getFor(Long userid);

    void save(Long userid, int count);
}
