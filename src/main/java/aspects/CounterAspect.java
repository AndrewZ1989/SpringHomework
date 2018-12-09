package aspects;

import domainModel.Event;
import domainModel.Ticket;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;


@Aspect
@Component
public class CounterAspect {

    private HashMap<Event, EventStatistics> _eventsStats;

    public CounterAspect(){
        _eventsStats = new HashMap<>();
    }


    @AfterReturning(pointcut = "execution(* domainServices.EventService.getByName(..))", returning = "ret")
    public void callGetByNameAfter(JoinPoint p, Object ret){
        if(!(ret instanceof Optional) ){
            return;
        }

        Optional<Event> eOpt = (Optional<Event>) ret;
        if(!eOpt.isPresent()){
            return;
        }

        Event e = eOpt.get();
        EventStatistics stat = getStatsFor(e);
        stat.setByNameAccessCount(stat.getByNameAccessCount() + 1);
    }

    @Before("execution(* domainServices.BookingService.getTicketsPrice(..))")
    public void callGetPriceBefore(JoinPoint p){
        Object eventArg = p.getArgs()[0];
        if(!(eventArg instanceof  Event)){
            return;
        }

        Event e = (Event)eventArg;
        EventStatistics stat = getStatsFor(e);
        stat.setPriceQueriedCount(stat.getPriceQueriedCount() + 1);
    }

    @Before("execution(* domainServices.BookingService.bookTickets(..))")
    public void callBookTicketsBefore(JoinPoint p){
        Object ticketsArg = p.getArgs()[0];
        if(!(ticketsArg instanceof Set)){
            return;
        }

        Set<Ticket> tickets = (Set<Ticket>) ticketsArg;

        for(Ticket t: tickets){
            Event e = t.getEvent();
            EventStatistics es = getStatsFor(e);

            es.setBookedTicketsCount( es.getBookedTicketsCount() + 1);
        }
    }


    private EventStatistics getStatsFor(Event e){
        if(!_eventsStats.containsKey(e)){
            _eventsStats.put(e, new EventStatistics());
        }
        return _eventsStats.get(e);
    }
}

class EventStatistics{
    private Integer byNameAccessCount;
    private Integer priceQueriedCount;
    private Integer bookedTicketsCount;

    public  EventStatistics(){
        byNameAccessCount = 0;
        priceQueriedCount = 0;
        bookedTicketsCount = 0;
    }

    public Integer getByNameAccessCount(){
        return byNameAccessCount;
    }
    public void setByNameAccessCount(Integer value){
        byNameAccessCount = value;
    }

    public Integer getPriceQueriedCount() {
        return priceQueriedCount;
    }
    public void setPriceQueriedCount(Integer priceQueriedCount) {
        this.priceQueriedCount = priceQueriedCount;
    }

    public Integer getBookedTicketsCount() {
        return bookedTicketsCount;
    }
    public void setBookedTicketsCount(Integer bookedTicketsCount) {
        this.bookedTicketsCount = bookedTicketsCount;
    }
}