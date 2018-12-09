package springConfig;

import aspects.CounterAspect;
import aspects.DiscountAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan
public class AppConfig {


    @Bean
    public String auditoriumConfigPath(){
        return "target/resources/auditorium.json";
    }

    @Bean
    public DiscountAspect discountAspect(){
        return  new DiscountAspect();
    }

    @Bean
    public CounterAspect counterAspect(){ return  new CounterAspect(); }

}
