package page.rightshift;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class WorldLoader {
    public void load(boolean loud) throws IOException, ParseException {
        // initialize the world, load it in from file
        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(new FileReader("d:\\temp\\world.json"));

        // initialize the world hashmap
        Main.world = new HashMap<>();

        for(Object o: array) {
            JSONObject obj = (JSONObject) o;
            Location l = new Location(obj);
            l.players = new LinkedList<>();

            Main.world.put(l.getId(), l);

            if(loud) {
                System.out.println("WorldLoader: Location id: " + l.getId());
                System.out.println("WorldLoader: Location title: " + l.getTitle());
                System.out.println("WorldLoader: Location body: " + l.getBody());
                for (String s : l.getExits()) {
                    System.out.println("WorldLoader: Exit #" + l.getExits().indexOf(s) + ": " + s);
                }
            }
        }
    }
}
