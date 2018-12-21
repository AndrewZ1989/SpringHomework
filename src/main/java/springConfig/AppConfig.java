package springConfig;

import aspects.*;
import aspectsRepositories.*;
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
    public UsersRepository usersRepository(DataSource ds){
        return new UsersRepositoryDbImpl(ds);
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
    public LuckyWinnerRepository luckyWinnerRepository(DataSource ds){
        return new LuckyWinnerRepositoryDb(ds);
    }

    @Bean
    public DiscountAspectRepository discountAspectRepository(DataSource ds){
        return new DiscountAspectRepositoryDb(ds);
    }

    @Bean
    public BookingRepository bookingRepository(DataSource ds, UsersRepository uRep, EventsRepository eRep) {
        return new BookingRepositoryDbImpl(ds, uRep, eRep);
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
    public LuckyWinnerAspect luckyWinnerAspect(LuckyWinnerRepository rep, UsersRepository uRep){
        return  new LuckyWinnerAspect(rep, uRep); }

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
