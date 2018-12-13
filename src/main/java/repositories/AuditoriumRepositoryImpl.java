package repositories;

import domainModel.Auditorium;
import org.springframework.stereotype.Component;

@Component
public class AuditoriumRepositoryImpl extends RepositoryImpl<Auditorium> implements AuditoriumRepository {
    @Override
    public Auditorium create() {
        long id = objCount.addAndGet(1);
        return new Auditorium(id);
    }

}
