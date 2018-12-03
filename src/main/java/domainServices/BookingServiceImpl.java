package domainServices;

import domainModel.*;
import domainServices.discount.DiscountService;
import domainServices.discount.DiscountsForSeats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class BookingServiceImpl implements BookingService {

    public BookingServiceImpl(DiscountService discountSvc){
        this.discountSvc = discountSvc;
        _storage = new ArrayList<>();
    }

    private ArrayList<Ticket> _storage;
    private static AtomicLong _ticketsCount = new AtomicLong(0);

    private static final double vipPriceCoefficient = 2;
    private static final double highRatedCoefficient = 1.2;

    private DiscountService discountSvc;

    @Override
    public Ticket createTicket(User user, Event event, LocalDateTime dateTime, long seat) {
        long id = _ticketsCount.addAndGet(1);
        return new Ticket(id, user, event, dateTime, seat);
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
        _storage.add(t);
    }

    @Nonnull
    @Override
    public Set<Ticket> getPurchasedTicketsForEvent(@Nonnull Event event, @Nonnull LocalDateTime dateTime) {
        Set<Ticket> result = new HashSet<>();

        for(Ticket t:_storage){
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
