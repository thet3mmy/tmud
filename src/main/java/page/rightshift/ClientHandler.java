package page.rightshift;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final DataInputStream in;
    private final PrintWriter out;

    public Player player;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream());
    }

    public PrintWriter getOut() {
        return out;
    }

    @Override
    public void run() {
        try {
            out.print("Player name? ");
            out.flush();
            player = new Player(this, in.readLine());
        } catch (Exception ignored) {}

        while(true) {
            try {
                out.print("? ");
                out.flush();

                String line = in.readLine();

                try {
                    String[] tokens = line.split("\\s+");

                    switch (tokens[0]) {
                        case "stat" -> player.printStats();
                        case "l" -> player.printLook();
                        case "ex" -> player.printExits();
                        case "i" -> player.printInventory();
                        case "leave" -> {
                            socket.close();
                            player.save();
                            return;
                        }
                        case "go" -> {
                            try {
                                // leave the current location
                                player.currentLocation.removeFromLocation(player);

                                player.currentLocation = Main.world.get(tokens[1]);

                                // add to the new location
                                player.currentLocation.addToLocation(player);
                            } catch (ArrayIndexOutOfBoundsException ignored){}
                        }
                        case "who" -> {
                            out.println("You are: " + player.name);
                            out.flush();

                            for (Player pl : player.currentLocation.players) {
                                out.println(pl.name);
                            }
                            out.flush();
                        }
                        case "drop" -> {
                            String droppingId = tokens[1];
                            if(player.inventory.contains(Item.getItem(droppingId))) {
                                player.inventory.remove(Item.getItem(droppingId));
                                player.currentLocation.items.add(Item.getItem(droppingId));
                                out.println("Dropped " + Item.getItem(droppingId).name);
                            } else {
                                out.println("You don't have any " + Item.getItem(droppingId).name);
                            }
                        }
                        case "take" -> {
                            String takingId = tokens[1];
                            Item i = Item.getItem(takingId);

                            if(player.currentLocation.items.contains(i)) {
                                player.inventory.add(i);
                                player.currentLocation.items.remove(i);
                                out.println("Took " + i.name);
                            } else {
                                out.println("There is no " + i.name);
                            }
                        }
                        case "give" -> {
                            String playerName = tokens[1];
                            String givingId = tokens[2];
                            Player targetPlayer = player.currentLocation.getPlayerByName(playerName);

                            if(targetPlayer != null) {
                                Item i = Item.getItem(givingId);
                                player.inventory.remove(i);
                                targetPlayer.inventory.add(i);

                                out.println("Gave " + targetPlayer.name + " your " + i.name);
                                out.flush();

                                targetPlayer.handler.getOut().println("You recieved " + i.name + " from " + player.name);
                                targetPlayer.handler.getOut().flush();
                            }
                        }
                        case "spawn" -> {
                            if(player.isAdmin) {
                                Item i = Item.getItem(tokens[1]);
                                player.inventory.add(i);
                                out.println("Given " + i.name);
                            }
                        }
                        case "wear" -> {
                            Item i = Item.getItem(tokens[1]);
                            if(player.inventory.contains(i)) {
                                if(i.armorType == 1) {
                                    player.equipHelmet(i);
                                } else if(i.armorType == 2) {
                                    player.equipChestplate(i);
                                }

                                out.println("You wear " + i.name);
                            } else {
                                out.println("You don't have " + i.name);
                            }

                            out.flush();
                        }
                        case "remove" -> {
                            try {
                                if (tokens[1].equals("helmet")) {
                                    player.helmet = Item.getItem("nothing");
                                } else if (tokens[1].equals("chestplate")) {
                                    player.chestplate = Item.getItem("nothing");
                                } else {
                                    out.println("Remove what?");
                                    out.flush();
                                }
                            } catch (ArrayIndexOutOfBoundsException ignored) {}
                        }
                    }
                } catch (NullPointerException e) {
                    socket.close();
                    player.save();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
