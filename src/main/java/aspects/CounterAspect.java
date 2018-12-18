package aspects;

import aspectsRepositories.CounterAspectRepository;
import domainModel.Event;
import domainModel.Ticket;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import repositories.EventsRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Aspect
@Component
public class CounterAspect {

    private CounterAspectRepository repository;
    private EventsRepository eventRep;

    @Autowired
    public CounterAspect(CounterAspectRepository repository,
                         EventsRepository eventRep){
        this.repository = repository;
        this.eventRep = eventRep;
    }

    public Map<Event, EventStatistics> getStatistics(){
        Map<Long, EventStatistics> stat = repository.getAll();
        Map<Event, EventStatistics> res = new HashMap<>();

        for(Map.Entry<Long, EventStatistics> entry : stat.entrySet()){
            Long key = entry.getKey();
            Optional<Event> evt = eventRep.tryGetFirst(e -> e.getId() == key);

            if(evt.isPresent()){
                res.put(evt.get(), entry.getValue());
            }
        }

        return res;
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

        saveStatsFor(e, stat);
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

        saveStatsFor(e, stat);
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

            saveStatsFor(e, es);
        }
    }


    private EventStatistics getStatsFor(Event e){
        if(!repository.hasDataFor(e)){
            return new EventStatistics();
        }
        return repository.getFor(e);
    }

    private void saveStatsFor(Event e, EventStatistics stats){
        repository.save(e, stats);
    }
}

