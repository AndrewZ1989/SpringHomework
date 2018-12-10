package domainServices.discount;

import domainModel.Event;
import domainModel.User;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class BirthdayStrategy implements DiscountStrategy {

    @Override
    public DiscountsForSeats getDiscount(@Nullable User user, @Nonnull Event event, @Nonnull LocalDateTime airDateTime, Set<Long> seats) {

        LocalDateTime date = user.getBirthDate();

        if(date.isBefore(airDateTime) && date.plusDays(5).isAfter(airDateTime)){
            return  discountMap(seats, 0.05);
        }

        if(date.isAfter(airDateTime) && date.minusDays(5).isBefore(airDateTime)){
            return  discountMap(seats, 0.05);
        }

        return discountMap(seats, 0.0);
    }

    @Override
    public String getName() {
        return "Birthday";
    }

    private DiscountsForSeats discountMap(Set<Long> seats, Double discount){
        HashMap<Long,Double> mp = new HashMap<>();
        for(Long seat : seats){
            mp.put(seat, discount);
        }

        return seat -> {
            if(mp.containsKey(seat)){
                return mp.get(seat);
            }
            return 0;
        };
    }

}
