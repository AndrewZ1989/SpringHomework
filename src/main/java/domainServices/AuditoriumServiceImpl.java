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
import repositories.AuditoriumRepository;

public class AuditoriumServiceImpl implements AuditoriumService {

    private AuditoriumRepository rep;

    public AuditoriumServiceImpl(String configPath, AuditoriumRepository rep){
        this.rep = rep;
        loadConfig(configPath);
    }


    @Override
    public Auditorium create() {
        return rep.create();
    }

    @Override
    public void add(Auditorium auditorium) {
        rep.save(auditorium);
    }

    @Nonnull
    @Override
    public Collection<Auditorium> getAll() {
        return rep.getAll();
    }

    @Nullable
    @Override
    public Auditorium getByName(@Nonnull String name) {
        Optional<Auditorium> a = rep.tryGetFirst(x -> x.getName().equals(name));
        return a.isPresent() ? a.get() : null;
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

        rep.save(aud);
    }



}
