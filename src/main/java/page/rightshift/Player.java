package page.rightshift;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class Player {
    public ClientHandler handler;
    public boolean isAdmin;
    public int classType;
    public String name;

    public Location currentLocation;
    public LinkedList<Item> inventory;

    public Item helmet;
    public Item chestplate;

    public static String[] classNames = {"KNIGHT", "MAGE"};

    public int hp;
    public int mana;

    public void printLook() {
        handler.getOut().println(currentLocation.getTitle());
        handler.getOut().println(currentLocation.getBody());
        handler.getOut().println("Items:");

        for(Item i: currentLocation.items) {
            handler.getOut().println(i.name + " (" + i.key + ")");
        }

        handler.getOut().flush();
    }

    public void printExits() {
        for(String x: currentLocation.getExits()) {
            Location l = Main.world.get(x);
            handler.getOut().println(l.getId() + " (" + l.getTitle() + ")");
            handler.getOut().flush();
        }
    }

    public int getTotalProtection() {
        // this COULD be a large function, so we will future-proof it for now
        return helmet.protection + chestplate.protection;
    }

    public void printStats() {
        handler.getOut().println("Player name: " + name);
        handler.getOut().println("Hit points: " + hp + " Mana: " + mana);
        handler.getOut().println("Protection: " + getTotalProtection());
        handler.getOut().println("Class: " + classNames[classType]);

        if(isAdmin)
            handler.getOut().println("You ARE an admin");

        handler.getOut().flush();
    }

    public void printInventory() {
        for(Item i: inventory) {
            handler.getOut().println(i.name + " (" + i.key + ")");
        }
        handler.getOut().flush();
    }

    public void save() throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("name", this.name);
        obj.put("hp", this.hp);
        obj.put("mana", this.mana);
        obj.put("admin", this.isAdmin);
        obj.put("class", this.classType);
        obj.put("location", this.currentLocation.getId());

        JSONArray array = new JSONArray();
        for(Item i: inventory) {
            array.add(i.key);
        }

        obj.put("inventory", array);
        obj.put("helmet", helmet.key);
        obj.put("chestplate", chestplate.key);

        FileWriter writer = new FileWriter("d:\\temp\\players\\" + name + ".json");
        writer.write(obj.toJSONString());
        writer.flush();
        writer.close();

        System.out.println(this + ": Saving information to file for player " + name);
    }

    public void equipHelmet(Item i) {
        if(i.protection > 0 && i.armorType == 1) {
            helmet = i;
        }
    }

    public void equipChestplate(Item i) {
        if(i.protection > 0 && i.armorType == 2) {
            chestplate = i;
        }
    }

    Player(ClientHandler h, String name) throws IOException, ParseException {
        inventory = new LinkedList<>();

        handler = h;
        System.out.println(this + ": Starting to initialize for player " + name);

        JSONParser parser = new JSONParser();

        JSONObject player = (JSONObject) parser.parse(new FileReader("d:\\temp\\players\\" + name + ".json"));
        hp = ((Long)player.get("hp")).intValue();
        mana = ((Long)player.get("mana")).intValue();
        isAdmin = (Boolean) player.get("admin");
        classType = ((Long)player.get("class")).intValue();

        helmet = Item.getItem("nothing");
        chestplate = Item.getItem("nothing");

        JSONArray inventoryArray = (JSONArray) player.get("inventory");
        for(Object o: inventoryArray) {
            inventory.add(Item.getItem((String)o));
        }

        helmet = Item.getItem((String)player.get("helmet"));
        chestplate = Item.getItem((String)player.get("chestplate"));

        this.name = name;

        h.getOut().println("Successfully loaded stats for player name: " + name);
        h.getOut().flush();

        currentLocation = Main.world.get((String)player.get("location"));
        currentLocation.addToLocation(this);
    }
}
