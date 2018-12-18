package aspects;

import aspectsRepositories.DiscountAspectRepository;
import domainModel.User;
import domainServices.discount.BirthdayStrategy;
import domainServices.discount.DiscountStrategy;
import domainServices.discount.DiscountsForSeats;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import repositories.UsersRepository;
import utility.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Aspect
@Component
public class DiscountAspect {

    private DiscountAspectRepository rep;
    private UsersRepository userRep;

    public DiscountAspect(DiscountAspectRepository rep,
                          UsersRepository userRep){
        this.rep = rep;
        this.userRep = userRep;
    }

    @AfterReturning(pointcut = "execution(* domainServices.discount.DiscountService.getDiscount(..))", returning = "retVal")
    public void callAfter(JoinPoint p, Object retVal){

        Tuple<DiscountsForSeats, DiscountStrategy> s = (Tuple<DiscountsForSeats, DiscountStrategy>) retVal;
        DiscountStrategy st = s.second;

        String strategyClassName = st.getName();

        Optional<HashMap<Long, Integer>> strategyStatistic = rep.getFor(strategyClassName);

        Object userObj = p.getArgs()[0];
        if(userObj == null){
            return;
        }

        User user = (User)userObj;

        Integer currentCount = 0;
        if(strategyStatistic.isPresent() && strategyStatistic.get().containsKey(user.getId())){
            currentCount = strategyStatistic.get().get(user.getId());
        }

        rep.save(strategyClassName, user.getId(), currentCount + 1);
    }

    public Map<String, HashMap<User, Integer>> getStatistics()
    {
        Map<String, HashMap<Long, Integer>> userStats = rep.getAll();

        Map<String, HashMap<User, Integer>> ans = new HashMap<>();
        for(Map.Entry<String, HashMap<Long, Integer>> s : userStats.entrySet() ){

            HashMap<User, Integer> m = new HashMap<>();
            for(Map.Entry<Long, Integer> e : s.getValue().entrySet()){
                Long userId = e.getKey();
                Optional<User> usr = userRep.tryGetFirst(u -> u.getId().equals(userId));
                if(usr.isPresent()){
                    m.put(usr.get(), e.getValue());
                }
            }

            ans.put(s.getKey(), m);
        }
        return ans;
    }


}
