package domainServices.discount;

import domainModel.Event;
import domainModel.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Set;

public interface DiscountStrategy {

    DiscountsForSeats getDiscount(@Nullable User user, @Nonnull Event event, @Nonnull LocalDateTime airDateTime, Set<Long> seats);

}
