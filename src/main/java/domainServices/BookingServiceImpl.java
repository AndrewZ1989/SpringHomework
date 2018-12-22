package domainServices;

import domainModel.*;
import domainServices.discount.DiscountService;
import domainServices.discount.DiscountStrategy;
import domainServices.discount.DiscountsForSeats;
import exceptions.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import repositories.AuditoriumRepository;
import repositories.BookingRepository;
import repositories.UsersRepository;
import utility.Tuple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class BookingServiceImpl implements BookingService {

    @Autowired
    public BookingServiceImpl(DiscountService discountSvc,
                              BookingRepository rep,
                              UsersRepository uRep,
                              AuditoriumRepository audRep){
        this.discountSvc = discountSvc;
        this.rep = rep;
        this.uRep = uRep;
        this.audRep = audRep;
    }


    private static final double vipPriceCoefficient = 2;
    private static final double highRatedCoefficient = 1.2;

    private DiscountService discountSvc;
    private BookingRepository rep;
    private UsersRepository uRep;
    private AuditoriumRepository audRep;

    @Override
    public Ticket createTicket(User user, Event event, LocalDateTime dateTime, long seat) {
        return rep.createTicket(user,event, dateTime, seat);
    }

    @Override
    public double getTicketsPrice(@Nonnull Event event, @Nonnull LocalDateTime dateTime, @Nullable User user, @Nonnull Set<Long> seats) throws ApplicationException {

        if(!event.getAuditoriumsIds().containsKey(dateTime)){
            //If there are no auditorium for event then all places are free :)
            return 0;
        }

        Tuple<DiscountsForSeats, DiscountStrategy> discounts = discountSvc.getDiscount(user, event, dateTime, seats );

        double totalPrice = 0;
        for( Long seat : seats){
            Double discount = discounts.first.getDiscountForSeat(seat);
           totalPrice += getTicketPrice( event, dateTime, user, seat, discount);
        }
        return  totalPrice;
    }
    private double getTicketPrice(Event event, LocalDateTime dateTime, User user, Long seat, Double discount) throws ApplicationException {

        Long audId = event.getAuditoriumsIds().get(dateTime);
        Optional<Auditorium> aud = audRep.tryGetFirst(a -> a.getId().equals(audId));
        if(!aud.isPresent())
        {
            throw new ApplicationException("There is no auditorium with provided id.");
        }


        //Apply the best discount
        double price = event.getBasePrice()*(1-discount);

        //All prices for high rated movies should be higher
        if(event.getRating() == EventRating.HIGH){
            price *= highRatedCoefficient;
        }

        //Vip seats should cost more than regular seats
        if(aud.get().getVipSeats().contains(seat)){
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
        u.getTicketsIds().add(t.getId());
        try {
            rep.save(t);
            uRep.save(u);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    @Override
    public Set<Ticket> getPurchasedTicketsForEvent(@Nonnull Event event, @Nonnull LocalDateTime dateTime) {
        Set<Ticket> result = new HashSet<>();

        for(Ticket t:rep.getAll()){
            for(LocalDateTime date : t.getEvent().getAuditoriumsIds().keySet()){
                if(date.isEqual(dateTime)){
                    result.add(t);
                    break;
                }
            }
        }
        return result;
    }
}
