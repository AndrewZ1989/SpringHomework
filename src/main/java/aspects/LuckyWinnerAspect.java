package aspects;

import aspectsRepositories.LuckyWinnerRepository;
import domainModel.Ticket;
import domainModel.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import repositories.UsersRepository;

import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
public class LuckyWinnerAspect {

    private Random rand;
    //private Map<User,Integer> statistics;
    private LuckyWinnerRepository rep;
    private UsersRepository userRep;

    public LuckyWinnerAspect(LuckyWinnerRepository rep,
                             UsersRepository userRep){
        this.rep = rep;
        this.userRep = userRep;
        rand = new Random();
    }

    public Map<User,Integer> getStatistics(){
        Map<Long,Integer> userInfo = rep.getAll();

        Map<User,Integer> res = new HashMap<>();
        for(Map.Entry<Long,Integer> e : userInfo.entrySet()){
            Long userId = e.getKey();
            Optional<User> usr = userRep.tryGetFirst(u -> u.getId().equals(userId));
            if(usr.isPresent()){
                res.put(usr.get(), e.getValue());
            }
        }
        return res;
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

               int count = 0;
               if(rep.containsFor(u.getId())){
                   count = rep.getFor(u.getId());
               }
               rep.save(u.getId(), count + 1);
           }
       }
    }


}
