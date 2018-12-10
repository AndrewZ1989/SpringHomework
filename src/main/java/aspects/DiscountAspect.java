package aspects;

import domainModel.User;
import domainServices.discount.BirthdayStrategy;
import domainServices.discount.DiscountStrategy;
import domainServices.discount.DiscountsForSeats;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import utility.Tuple;

import java.util.HashMap;
import java.util.Map;


@Aspect
@Component
public class DiscountAspect {

    private Map<String, HashMap<User, Integer>> statistics;

    public DiscountAspect(){
        statistics = new HashMap<>();
    }

    @AfterReturning(pointcut = "execution(* domainServices.discount.DiscountService.getDiscount(..))", returning = "retVal")
    public void callAfter(JoinPoint p, Object retVal){

        Tuple<DiscountsForSeats, DiscountStrategy> s = (Tuple<DiscountsForSeats, DiscountStrategy>) retVal;
        DiscountStrategy st = s.second;

        String strategyClassName = st.getName();

        if(!statistics.containsKey(strategyClassName)){
            statistics.put(strategyClassName, new HashMap<>());
        }

        HashMap<User, Integer> strategyStatistic = statistics.get(strategyClassName);

        Object userObj = p.getArgs()[0];
        if(userObj == null){
            return;
        }

        User user = (User)userObj;

        if(!strategyStatistic.containsKey(user)){
            strategyStatistic.put(user, 0);
        }

        Integer value = strategyStatistic.get(user);
        value += 1;

        strategyStatistic.put(user, value);
    }

    public Map<String, HashMap<User, Integer>> getStatistics(){
        return statistics;
    }


}
