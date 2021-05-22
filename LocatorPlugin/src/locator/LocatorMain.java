package locator;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocatorMain extends JavaPlugin {
    IOListener jl;
    static Logger logger;
    static JavaPlugin thisPlugin;
    @Override
    public void onEnable() {
        thisPlugin = this;
        logger = getLogger();
        LocationLog banners = new LocationLog();
        try {
            banners = (LocationLog) IOListener.loadLog("banners");
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE,"Was not able to load banners",e);
        }
        CommandLocation commandLocation = new CommandLocation(banners,new NamespacedKey(this,"ModifiedCompass"));
        jl = new IOListener(commandLocation);
        getServer().getPluginManager().registerEvents(jl, this);
        this.getCommand("locate").setExecutor(commandLocation);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> getServer().getOnlinePlayers().forEach(player -> jl.savePlayerLog(player,true)));
    }


}

class IOListener implements Listener{
    CommandLocation cl;
    public static final String FILE_PATTERN = "LocationLogs/%s.log";

    public IOListener(CommandLocation cl) {
        this.cl = cl;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        UUID uniqueId = event.getPlayer().getUniqueId();
        LocationLog log = null;
        try {
            log = (LocationLog) loadLog(uniqueId.toString());
        }catch (ClassNotFoundException|IOException e){
            LocatorMain.logger.log(Level.SEVERE,"Was not able to load log"+event.getPlayer().getName(),e);
        }
        if (log == null) log = new LocationLog();
        cl.myLogs.put(uniqueId,log);
    }


    public static Object loadLog(String name) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(String.format(FILE_PATTERN, name));
             ObjectInputStream input = new ObjectInputStream(fis)) {
            return input.readObject();
        }catch (FileNotFoundException fnfe){
            return new LocationLog();
        }
    }

    @EventHandler
    public void onPlayerExit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(LocatorMain.thisPlugin,()->savePlayerLog(event.getPlayer(),true));
    }
    public void savePlayerLog(Player player,boolean destructive){
        UUID uuid = player.getUniqueId();
        LocationLog save = destructive?cl.myLogs.remove(uuid):cl.myLogs.get(uuid);
        try {
            saveLog(uuid.toString(),save);
        } catch (IOException e) {
            LocatorMain.logger.log(Level.SEVERE,"Was not able to save log for player"+player.getName(),e);
        }
    }

    public static boolean saveLog(String name,Object log) throws IOException{
        try (FileOutputStream fos = new FileOutputStream(String.format(FILE_PATTERN, name));
             ObjectOutputStream output = new ObjectOutputStream(fos)) {
            output.writeObject(log);
            return true;
        }
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(LocatorMain.thisPlugin, () -> {
            event.getWorld().getPlayers().forEach(p -> savePlayerLog(p, false));
            try {
                saveLog("banners",cl.banners);
            } catch (IOException e) {
                LocatorMain.logger.log(Level.SEVERE,"Was not able to save banners",e);
            }
        });

    }


}



class CommandLocation implements CommandExecutor {
    HashMap<UUID, LocationLog> myLogs;
    int mapMarkCount;
    LocationLog banners;
    NamespacedKey key;

    public CommandLocation(LocationLog banners, NamespacedKey key) {
        myLogs = new HashMap<>();
        this.key =key;
        this.banners = banners;
        mapMarkCount = 0;
    }

    enum Task {
        store(false), storeTemp(true), list, myLoc, allLoc, remove(true), get(false), markToStore(false), storeToMark(true), mark, clear;

        private final Boolean util;

        private Task(boolean util) {
            this.util = util;
        }

        private Task() {
            util = null;
        }
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String taskString, String[] args) {
        Player player = (Player) commandSender;
        Task task;
        try {
            task = Task.valueOf(taskString);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Illegal task "+taskString+". Please specify one of "+ Arrays.toString(Task.values()) +"\n");
            return false;
        }
        return execCommand(player,task,args);
    }
    public boolean execCommand(Player player,Task task,String[] args){

        LocationLog log = myLogs.get(player.getUniqueId());
        if(log == null){
            LocatorMain.logger.log(Level.WARNING,String.format("Player %s with UUID %s is not loaded. " +
                    "Trying again in command executor",player.getName(),player.getUniqueId().toString()));
            log = new LocationLog();

        }

        String tag, comment;
        StoredLocation sl;
        String[] msgs;

        switch (task) {

            case store:
            case storeTemp:
                tag = args.length >= 1 ? args[0] : null;
                Location myLoc;
                if (args.length >= 4) {
                    myLoc = new Location(player.getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                    comment = args.length >= 5 ? args[5] : null;
                } else {
                    myLoc = player.getLocation();
                    comment = args.length >= 2 ? args[1] : null;
                }
                sl = new StoredLocation(task.util, LocalDateTime.now(), myLoc, comment, tag);
                World w = log.putInList(tag, sl);
                if (w != null) {
                    player.sendMessage(String.format("%s does not match the world (%s) that tag %s is assigned to\n", myLoc, w.getName(), tag));
                    return true;
                }
                String duration = task.util ? "For 24 hours\n" : "";
                player.sendMessage("Stored \n" + sl.toString() + duration);

                return true;

            case markToStore:
            case storeToMark:
                tag = args.length >= 1 ? args[0] : null;
                List<StoredLocation> mk = log.get(tag);
                if (mk == null) {
                    player.sendMessage("No stored locations for tag " + tag + "\n");
                    return true;
                }
                mk.forEach(s -> {
                    s.temp = task.util;
                    s.created = LocalDateTime.now();
                });
                msgs = mk.stream().map(StoredLocation::toString).toArray(String[]::new);
                String dur = task.util ? "permanently" : "for 24 hours";
                player.sendMessage(String.format("Stored %d location(s) with tag %s %s\n", mk.size(), tag, dur));
                player.sendMessage(msgs);
                return true;

            case remove:
            case get:
                tag = args.length >= 1 ? args[0] : null;
                ArrayList<StoredLocation> list;
                if (task.util)
                    list = log.remove(tag);
                else
                    list = log.get(tag);
                if (list == null) {
                    player.sendMessage("No stored locations for tag " + tag + "\n");
                    return true;
                }
                msgs = list.stream().map(StoredLocation::toString).toArray(String[]::new);
                String fxn = task.util ? "Removed" : "Displaying";
                player.sendMessage(String.format("%s %d location(s) with tag %s:\n", fxn, list.size(), tag));
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
                player.sendMessage(player.getLocation().toString() + "\n");
                return true;

            case allLoc:
                player.getWorld().getPlayers().forEach(p -> p.sendMessage(p.getDisplayName() + " requests " + p.getLocation().toString() + "\n"));
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
                        cmp.getPersistentDataContainer().set(key, PersistentDataType.BYTE,(byte)1);
                        cmp.setLodestoneTracked(false);
                        cmp.setLodestone(cmpLoc);
                        player.sendMessage(String.format("Marked %s on compass\n", marks.get(0).toString()));
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
                        if (mpWorld == null)
                            throw new NullPointerException("World of " + marks.get(0).toString() + "is null\n");
                        if (mv == null || mv.getWorld() == null || !mv.getWorld().equals(mpWorld)) {
                            mv = Bukkit.createMap(mpWorld);
                        }
                        mv.setUnlimitedTracking(true);

                        Material[] mats = new Material[]{Material.BLACK_BANNER, Material.ORANGE_BANNER, Material.MAGENTA_BANNER, Material.LIGHT_BLUE_BANNER, Material.YELLOW_BANNER, Material.LIME_BANNER, Material.PINK_BANNER, Material.GRAY_BANNER, Material.LIGHT_GRAY_BANNER, Material.CYAN_BANNER, Material.PURPLE_BANNER, Material.BLUE_BANNER, Material.BROWN_BANNER, Material.GREEN_BANNER, Material.RED_BANNER, Material.WHITE_BANNER};
                        BlockData bd = mats[mapMarkCount].createBlockData();
                        //TODO DYNMAP
                        marks.forEach(location -> {
                            player.sendBlockChange(location.myLocation, bd);
                            banners.putInList(player.getUniqueId().toString(), location);
                        });

                        msgs = marks.stream().map(StoredLocation::toString).toArray(String[]::new);
                        player.sendMessage(String.format("Marked %d location(s) with %s with tag %s:\n",
                                marks.size(), mats[mapMarkCount].toString().toLowerCase(Locale.ROOT).replace('_', ' '), tag));
                        player.sendMessage(msgs);
                        mapMarkCount++;
                    }
                    if (mm == null && cmp == null)
                        player.sendMessage("Please hold a Compass or a Map in one hand\n");


                } else
                    player.sendMessage("No stored locations for tag " + tag + "\n");

                return true;
            case clear:
                player.getInventory().forEach(item->{
                    Byte b =Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(key,PersistentDataType.BYTE);
                    if(b!=null && b==1){
                        ((CompassMeta)item.getItemMeta()).setLodestone(null);
                }});
                banners.get(player.getUniqueId().toString()).forEach(location -> player.sendBlockChange(location.myLocation,Material.AIR.createBlockData()));
        }
        return false;
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
class StoredLocation implements Serializable {
    boolean temp;
    LocalDateTime created;
    Location myLocation;
    String comment;
    String tag;

    public StoredLocation(Location loc){
        myLocation = loc;
    }

    public StoredLocation(boolean temp, LocalDateTime created, Location myLocation,String comment,String tag){
        this.temp = temp;
        this.created = created;
        this.myLocation = myLocation;
        this.comment=comment;
        this.tag = tag;
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