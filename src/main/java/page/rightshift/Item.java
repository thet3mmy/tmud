package page.rightshift;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Item {
    public static HashMap<String, Item> itemDict;

    public String key;
    public String name;
    public int armorType;
    public int damage;
    public int manaCost;
    public int protection;

    public static void initSystem() throws IOException, ParseException {
        itemDict = new HashMap<>();
        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(new FileReader("d:\\temp\\items.json"));

        for(Object o: array) {
            JSONObject obj = (JSONObject) o;

            String id = ((String)obj.get("id"));
            String name = ((String)obj.get("name"));
            int dmg = ((Long)obj.get("damage")).intValue();
            int mc = ((Long)obj.get("manaCost")).intValue();
            int pr = ((Long)obj.get("protection")).intValue();
            int at = ((Long)obj.get("armortype")).intValue();

            itemDict.put(id, new Item(id, name, dmg, mc, pr, at));
        }
    }

    public static Item getItem(String id) {
        return itemDict.get(id);
    }

    Item(String id, String name, int dmg, int mc, int pr, int at) {
        key = id;
        this.name = name;
        damage = dmg;
        manaCost = mc;
        protection = pr;
        armorType = at;
    }
}
