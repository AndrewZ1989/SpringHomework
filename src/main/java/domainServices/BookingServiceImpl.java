package domainServices;

import domainModel.*;
import domainServices.discount.DiscountService;
import domainServices.discount.DiscountsForSeats;
import repositories.BookingRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class BookingServiceImpl implements BookingService {

    public BookingServiceImpl(DiscountService discountSvc, BookingRepository rep){
        this.discountSvc = discountSvc;
        this.rep = rep;
    }


    private static final double vipPriceCoefficient = 2;
    private static final double highRatedCoefficient = 1.2;

    private DiscountService discountSvc;
    private BookingRepository rep;

    @Override
    public Ticket createTicket(User user, Event event, LocalDateTime dateTime, long seat) {
        return rep.createTicket(user,event, dateTime, seat);
    }

    @Override
    public double getTicketsPrice(@Nonnull Event event, @Nonnull LocalDateTime dateTime, @Nullable User user, @Nonnull Set<Long> seats) {

        if(!event.getAuditoriums().containsKey(dateTime)){
            //If there are no auditorium for event then all places are free :)
            return 0;
        }

        DiscountsForSeats discounts = discountSvc.getDiscount(user, event, dateTime, seats );

        double totalPrice = 0;
        for( Long seat : seats){
            Double discount = discounts.getDiscountForSeat(seat);
           totalPrice += getTicketPrice( event, dateTime, user, seat, discount);
        }
        return  totalPrice;
    }
    private double getTicketPrice(Event event, LocalDateTime dateTime, User user, Long seat, Double discount) {

        Auditorium aud = event.getAuditoriums().get(dateTime);

        //Apply the best discount
        double price = event.getBasePrice()*(1-discount);

        //All prices for high rated movies should be higher
        if(event.getRating() == EventRating.HIGH){
            price *= highRatedCoefficient;
        }

        //Vip seats should cost more than regular seats
        if(aud.getVipSeats().contains(seat)){
            price *= vipPriceCoefficient;
        }

        return price;
    }

    @Override
    public void bookTickets(@Nonnull Set<Ticket> tickets) {
        for(Ticket t: tickets){
          bookTicket(t);
        }
    }
    private void bookTicket(Ticket t) {
        User u = t.getUser();
        u.getTickets().add(t);
        rep.save(t);
    }

    @Nonnull
    @Override
    public Set<Ticket> getPurchasedTicketsForEvent(@Nonnull Event event, @Nonnull LocalDateTime dateTime) {
        Set<Ticket> result = new HashSet<>();

        for(Ticket t:rep.getAll()){
            for(LocalDateTime date : t.getEvent().getAuditoriums().keySet()){
                if(date.isEqual(dateTime)){
                    result.add(t);
                    break;
                }
            }
        }
        return result;
    }
}
