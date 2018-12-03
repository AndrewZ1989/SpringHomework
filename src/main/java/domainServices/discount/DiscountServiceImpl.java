package domainServices.discount;

import domainModel.Event;
import domainModel.User;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DiscountServiceImpl implements DiscountService {

    public DiscountServiceImpl(Collection<DiscountStrategy> strategies){
        _strategies = strategies;
    }

    private Collection<DiscountStrategy> _strategies;


    @Override
    public DiscountsForSeats  getDiscount(User user, @Nonnull Event event, @Nonnull LocalDateTime airDateTime, Set<Long> seats) {

        DiscountsForSeats  discounts = seat -> 0;
        double price = -1;

        for(DiscountStrategy strategy : _strategies){
            DiscountsForSeats currentDiscounts = strategy.getDiscount(user, event, airDateTime, seats);

            if(price < 0){
                discounts = currentDiscounts;
                price = getPrice(event, currentDiscounts, seats);
            }
            else {
                Double currentPrice = getPrice(event, currentDiscounts, seats);
                if(currentPrice < price){
                    discounts = currentDiscounts;
                    price = currentPrice;
                }
            }
        }

        return discounts;
    }

    private double getPrice(Event e, DiscountsForSeats discounts, Set<Long> seats){
        double totalPrice = 0;

        double basePrice = e.getBasePrice();
        for(Long seat : seats){
            Double discount = discounts.getDiscountForSeat(seat);
            totalPrice += basePrice * (1-discount);
        }
        return totalPrice;
    }

}
