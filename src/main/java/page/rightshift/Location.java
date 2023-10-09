package page.rightshift;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.LinkedList;

public class Location {
    private final String id;
    private final String title;
    private final String body;
    private final LinkedList<String> exitIds;

    public LinkedList<Player> players;
    public LinkedList<Item> items;

    Location(JSONObject o) {
        id = (String)o.get("id");
        title = (String)o.get("title");
        body = (String)o.get("body");

        exitIds = new LinkedList<>();
        items = new LinkedList<>();

        JSONArray array = (JSONArray) o.get("exits");
        for(Object obj: array) {
            exitIds.add((String)obj);
        }
    }

    public void addToLocation(Player p) {
        for(Player pl: players) {
            pl.handler.getOut().println(p.name + " arrives");
            pl.handler.getOut().flush();
        }
        players.add(p);
    }

    public void removeFromLocation(Player p) {
        players.remove(p);
        for(Player pl: players) {
            pl.handler.getOut().println(p.name + " leaves");
            pl.handler.getOut().flush();
        }
    }

    public Player getPlayerByName(String name) {
        for(Player pl: players) {
            if(pl.name.equals(name)) {
                return pl;
            }
        }
        return null;
    }

    public String getId()                   { return id; }
    public String getTitle()                { return title; }
    public String getBody()                 { return body; }
    public LinkedList<String> getExits()    { return new LinkedList<>(exitIds); }
}
