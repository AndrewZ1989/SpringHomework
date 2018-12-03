package domainServices.discount;

import java.time.LocalDateTime;
import java.util.*;

import javax.annotation.Nonnull;

import domainModel.Event;
import domainModel.User;

/**
 * @author Yuriy_Tkach
 */
public interface DiscountService {

    /**
     * Returns the best discount for each seat
     * @param user
     * @param event
     * @param airDateTime
     * @param seats
     * @return
     */
    DiscountsForSeats  getDiscount(User user, @Nonnull Event event, @Nonnull LocalDateTime airDateTime, Set<Long> seats);

}