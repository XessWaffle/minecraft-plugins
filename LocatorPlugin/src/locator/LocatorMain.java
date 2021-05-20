package locator;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class LocatorMain extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


}

class JoinListener implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

    }
}


class CommandBroadcast implements CommandExecutor {
}

class CommandLocation implements CommandExecutor {
    HashMap<UUID, LocationLog> myLogs;

    public CommandLocation() {
        myLogs = new HashMap<>();
    }

    enum Task {
        store(false), storeTemp(true), list, myLoc, allLoc, remove(true), get(false), markToStore(false), storeToMark(true), mark;

        private final Boolean util;

        private Task(boolean util) {
            this.util = util;
        }

        private Task() {
            util = null;
        }
    }



    public boolean onCommand(CommandSender commandSender, Command command, String taskString, String[] args) {
        Player player = (Player) commandSender;
        LocationLog log = myLogs.get(player.getUniqueId());
        Task task;

        try {
            task = Task.valueOf(taskString);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Illegal task "+taskString+". Please specify one of "+ Arrays.toString(Task.values()) +"\n");
            return false;
        }
        String tag, comment;
        StoredLocation sl;
        String[] msgs;

        ArrayList<String> output = new ArrayList<>();
        switch (task) {

            case store: case storeTemp:
                tag = args.length >= 1 ? args[0] : null;
                Location myLoc;
                if (args.length >= 4) {
                    myLoc = new Location(player.getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                    comment = args.length >= 5 ? args[5] : null;
                } else {
                    myLoc = player.getLocation();
                    comment = args.length >= 2 ? args[1] : null;
                }
                sl = new StoredLocation(task.util, LocalDateTime.now(), myLoc, comment);
                World w = log.putInList(tag, sl);
                if(w!=null){
                    player.sendMessage(String.format("%s does not match the world (%s) that tag %s is assigned to\n",myLoc,w.getName(),tag));
                    return false;
                }
                String duration = task.util?"For 24 hours\n":"";
                player.sendMessage("Stored \n" + sl.toString()+duration);

                return true;

            case markToStore: case storeToMark:
                tag = args.length >= 1 ? args[0] : null;
                List<StoredLocation> mk = log.get(tag);
                if(mk == null){
                    player.sendMessage("No stored locations for tag "+tag+"\n");
                    return false;
                }
                mk.forEach(s -> {s.temp = task.util;s.created=LocalDateTime.now();});
                msgs = mk.stream().map(StoredLocation::toString).toArray(String[]::new);
                String dur = task.util?"permanently":"for 24 hours";
                player.sendMessage(String.format("Stored %d location(s) with tag %s %s\n", mk.size(), tag,dur));
                player.sendMessage(msgs);
                return true;

            case remove: case get:
                tag = args.length >= 1 ? args[0] : null;
                ArrayList<StoredLocation> list;
                if(task.util)
                    list = log.remove(tag);
                else
                    list = log.get(tag);
                if (list == null) {
                    player.sendMessage("No stored locations for tag "+tag+"\n");
                    return false;
                }
                msgs = list.stream().map(StoredLocation::toString).toArray(String[]::new);
                String fxn = task.util ? "Removed" : "Displaying";
                player.sendMessage(String.format("%s %d location(s) with tag %s\n", fxn, list.size(), tag));
                player.sendMessage(msgs);
                return true;


            case list:
                for (String key : log.keySet()) {
                    ArrayList<StoredLocation> disp = log.get(key);
                    msgs = disp.stream().map(StoredLocation::toString).toArray(String[]::new);
                    player.sendMessage(String.format("Listing %d location(s) with tag %s\n", disp.size(), key));
                    player.sendMessage(msgs);
                }

                return true;
            case myLoc:
                player.sendMessage(player.getLocation().toString()+"\n");
                return true;

            case allLoc:
                player.getWorld().getPlayers().forEach(p -> p.sendMessage(p.getDisplayName() + " requests " + p.getLocation().toString()+"\n"));
                return true;

            case mark:
                tag = (args.length >= 1) ? args[0] : null;
                ArrayList<StoredLocation> marks = log.get(tag);
                if (marks != null) {
                    PlayerInventory inv = player.getInventory();
                    Location cmpLoc = marks.get(0).myLocation;
                    CompassMeta cmp = null;
                    if (inv.getItemInMainHand().getType() == Material.COMPASS)
                        cmp = (CompassMeta) inv.getItemInMainHand().getItemMeta();
                    else if (inv.getItemInOffHand().getType() == Material.COMPASS) {
                        cmp = (CompassMeta) inv.getItemInOffHand().getItemMeta();
                    }
                    if (cmp != null) {
                        cmp.setLodestoneTracked(false);
                        cmp.setLodestone(cmpLoc);
                    }

                    MapMeta mm = null;
                    if (inv.getItemInMainHand().getType() == Material.MAP)
                        mm = (MapMeta) inv.getItemInMainHand().getItemMeta();
                    else if (inv.getItemInOffHand().getType() == Material.MAP) {
                        mm = (MapMeta) inv.getItemInOffHand().getItemMeta();
                    }
                    if (mm != null) {
                        MapView mv = mm.getMapView();
                        World mpWorld = marks.get(0).myLocation.getWorld();
                        assert mpWorld != null;
                        if(mv==null||mv.getWorld()==null||!mv.getWorld().getUID().equals(mpWorld.getUID())){
                            mv = Bukkit.createMap(mpWorld);

                        }



                        mv.setUnlimitedTracking(true);

                    }

                    return true;

                }
                return false;

        }

    }
}

class LocRender extends MapRenderer{

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {

    }
}

class LocationLog extends HashMap<String, ArrayList<StoredLocation>> {
    public World putInList(String tag, StoredLocation location) {
        if(putIfAbsent(tag, new ArrayList<>())!=null&&get(tag).size()!=0){
            World world = Objects.requireNonNull(get(tag).get(0).myLocation.getWorld());
            UUID newWorld = Objects.requireNonNull(get(tag).get(0).myLocation.getWorld()).getUID();
            if(!world.getUID().equals(newWorld))
                return world;
        }
        get(tag).add(location);
        return null;
    }
}
class StoredLocation{
    boolean temp;
    LocalDateTime created;
    Location myLocation;
    String comment;

    public StoredLocation(boolean temp, LocalDateTime created, Location myLocation,String comment){
        this.temp = temp;
        this.created = created;
        this.myLocation = myLocation;
        this.comment=comment;
    }
    public boolean outDated(){
        return LocalDateTime.now().isAfter(created.plusDays(1));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if(temp)
            str.append("Temp");
         str.append(String.format("Location: %f, %f, %f in %s\nCreated At: %s\n",myLocation.getX(),myLocation.getY(),myLocation.getZ(), Objects.requireNonNull(myLocation.getWorld()).getName(),created.toString()));
         if(comment != null){
             str.append(String.format("Comment: %s\n",comment));
         }
         return str.toString();
    }
}