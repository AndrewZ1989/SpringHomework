package domainServices;

import domainModel.Auditorium;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AuditoriumServiceImpl implements AuditoriumService {

    public AuditoriumServiceImpl(String configPath){
        _storage = new TreeMap<>();

        loadConfig(configPath);
    }

    private TreeMap<String,Auditorium> _storage;
    private static AtomicLong _usersCount = new AtomicLong(0);

    @Override
    public Auditorium create() {
        long id = _usersCount.addAndGet(1);
        return new Auditorium(id);
    }

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


    private void loadConfig(String configPath) {
        File file = new File(configPath);
        if(file.isDirectory() || !file.exists()){
            return;
        }

        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(configPath))
        {
            Object obj = jsonParser.parse(reader);
            JSONArray auditoriumList = (JSONArray) obj;
            auditoriumList.forEach( emp -> parseAuditoriumObject( (JSONObject) emp ) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void parseAuditoriumObject(JSONObject employee)
    {
        JSONObject auditoriumObject = (JSONObject) employee.get("auditorium");

        String name = (String) auditoriumObject.get("name");
        Long numberOfSeats = (Long) auditoriumObject.get("numberOfSeats");
        JSONArray vipSeatsObj = (JSONArray) auditoriumObject.get("vipSeats");

        Set<Long> vipSeats = new HashSet<>();
        for(Object obj : vipSeatsObj){
            Long seat = (Long)obj;
            vipSeats.add(seat);
        }

        Auditorium aud = create();
        aud.setName(name);
        aud.setNumberOfSeats(numberOfSeats);
        aud.setVipSeats(vipSeats);
        _storage.put(name, aud);
    }



}
