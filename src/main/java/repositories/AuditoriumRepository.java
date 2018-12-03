package repositories;

import domainModel.Auditorium;

public interface AuditoriumRepository extends Repository<Auditorium> {
    Auditorium create();
}
