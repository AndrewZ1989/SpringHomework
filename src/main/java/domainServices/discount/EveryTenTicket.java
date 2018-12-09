package domainServices.discount;

import domainModel.Event;
import domainModel.User;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class EveryTenTicket implements DiscountStrategy {

    @Override
    public DiscountsForSeats getDiscount(@Nullable User user, @Nonnull Event event, @Nonnull LocalDateTime airDateTime, Set<Long> seats) {

        HashMap<Long,Double> mp = new HashMap<>();

        int seqNum = 1;
        for(Long seat : seats){
            if(seqNum % 10 == 0){
                mp.put(seat, 0.5);
            }else {
                mp.put(seat, 0.0);
            }
            seqNum++;
        }
        return seat -> {
            if(mp.containsKey(seat)){
                return mp.get(seat);
            }
            return 0;
        };
    }
}
