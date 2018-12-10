package aspects;

import domainModel.Ticket;
import domainModel.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
public class LuckyWinnerAspect {

    private Random rand;
    private Map<User,Integer> statistics;

    public LuckyWinnerAspect(){
        rand = new Random();
        statistics = new HashMap<>();
    }

    public Map<User,Integer> getStatistics(){
        return statistics;
    }

    @Around("execution(* domainServices.BookingService.bookTickets(..))")
    public void callBookTicketAround(ProceedingJoinPoint p) throws Throwable{
        tryGetLuck(p);
        p.proceed();
    }

    private void tryGetLuck(ProceedingJoinPoint p) {
        Object arg = p.getArgs()[0];
        if(!(arg instanceof Set)){
            return;
        }

        Set<Ticket> tickets = (Set<Ticket>) arg;

       List<User> uniqueUsers = tickets.stream().map(x -> x.getUser()).distinct().collect(Collectors.toList());

       for(User u : uniqueUsers){
           int rnd = rand.nextInt();
           if( rnd % 2 == 0) {
               if(!statistics.containsKey(u)){
                   statistics.put(u, 0);
               }
               statistics.put(u, statistics.get(u) + 1);
           }
       }
    }


}
