package springConfig;

import aspects.*;
import aspectsRepositories.CounterAspectRepository;
import aspectsRepositories.CounterAspectRepositoryDb;
import aspectsRepositories.DiscountAspectRepository;
import aspectsRepositories.DiscountAspectRepositoryDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import repositories.*;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan
public class AppConfig {

    @Bean
    public EventsRepository eventsRepository(){
        return new EventsRepositoryImpl();
    }

    @Bean
    public UsersRepository usersRepository(){
        return new UsersRepositoryImpl();
    }

    @Bean
    public AuditoriumRepository auditoriumRepository(DataSource ds) throws SQLException {
        return new AuditoriumRepositoryDbImpl(ds);
    }

    @Bean
    public CounterAspectRepository counterAspectRepository(DataSource ds){
        return new CounterAspectRepositoryDb(ds);
    }

    @Bean
    public DiscountAspectRepository discountAspectRepository(DataSource ds){
        return new DiscountAspectRepositoryDb(ds);
    }

    @Bean
    public BookingRepository bookingRepository(){
        return new BookingRepositoryImpl();
    }




    @Bean
    public String auditoriumConfigPath() {
        return "target/resources/auditorium.json";
    }

    @Bean
    public DiscountAspect discountAspect(DiscountAspectRepository rep, UsersRepository userRep){
        return  new DiscountAspect(rep, userRep);
    }

    @Bean
    public CounterAspect counterAspect(CounterAspectRepository counterRep, EventsRepository eventsRep){
        return  new CounterAspect(counterRep, eventsRep);
    }

    @Bean
    public LuckyWinnerAspect luckyWinnerAspect(){
        return  new LuckyWinnerAspect(); }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        dataSource.setUrl("jdbc:derby:\\database\\db;create=true");
        dataSource.setUsername("");
        dataSource.setPassword("");

        return dataSource;
    }

}
