package repositories;

import domainModel.Auditorium;

public class AuditoriumRepositoryImpl extends RepositoryImpl<Auditorium> implements AuditoriumRepository {
    @Override
    public Auditorium create() {
        long id = _objCount.addAndGet(1);
        return new Auditorium(id);
    }

}
