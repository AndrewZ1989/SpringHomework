package domainServices;

import domainModel.Auditorium;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class AuditoriumServiceImpl implements AuditoriumService {

    public AuditoriumServiceImpl(){
        _storage = new TreeMap<String,Auditorium>();
    }

    private TreeMap<String,Auditorium> _storage;

    @Override
    public void add(Auditorium auditorium) {
        _storage.put(auditorium.getName(), auditorium);
    }

    @Nonnull
    @Override
    public Collection<Auditorium> getAll() {
        return _storage.values();
    }

    @Nullable
    @Override
    public Auditorium getByName(@Nonnull String name) {
        if(_storage.containsKey(name)){
            return _storage.get(name);
        }
        return null;
    }
}
