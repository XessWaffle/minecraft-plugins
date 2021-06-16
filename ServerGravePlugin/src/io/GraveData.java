package io;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import gravemanager.GraveCreatorPlugin;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import utils.Grave;
import utils.InventorySerialization;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class GraveData {

    private static final String FILE_NAME = "gravedata.json";

    private JsonArray graveData;

    private GraveCreatorPlugin ref;

    public GraveData(GraveCreatorPlugin ref){
        graveData = new JsonArray();
        this.ref = ref;

        File create = new File(ref.getDataFolder() + "/" + FILE_NAME);

        if(!create.exists()){
            try {
                create.createNewFile();
                writeFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                InputStream is = new FileInputStream(create);
                InputStreamReader isr = new InputStreamReader(is);

                JsonParser jp = new JsonParser();
                graveData = jp.parse(new JsonReader(isr)).getAsJsonArray();


            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }

    private void writeFile() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String toWrite = gson.toJson(graveData);

        File create = new File(ref.getDataFolder() + "/" + FILE_NAME);

        try{
            FileWriter myWriter = new FileWriter(create);
            myWriter.write(toWrite);
            myWriter.close();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public boolean hasPlayer(String uuid){
        for(int i = 0; i < graveData.size(); i++){
            JsonObject in = graveData.get(i).getAsJsonObject();

            if(in.get("name").getAsString().equals(uuid)){
                return true;
            }

        }

        return false;
    }

    public JsonObject findPlayer(String uuid){
        for(int i = 0; i < graveData.size(); i++){
            JsonObject in = graveData.get(i).getAsJsonObject();

            if(in.get("name").getAsString().equals(uuid)){
                return in;
            }

        }

        return null;
    }

    public ArrayList<Location> getGraves(Player p){
        JsonArray target = findPlayer(p.getUniqueId().toString()).get("graves").getAsJsonArray();
        ArrayList<Location> locs = new ArrayList<>();
        for(int i = 0; i < target.size(); i++){
            JsonObject graveDat = target.get(i).getAsJsonObject();
            JsonObject location = graveDat.get("location").getAsJsonObject();

            locs.add(new Location(Bukkit.getWorld(location.get("world").getAsString()), location.get("x").getAsDouble(), location.get("y").getAsDouble(), location.get("z").getAsDouble()));
        }

        return locs;

    }

    public ArrayList<Location> getGraves(OfflinePlayer offlinePlayer) {

        JsonArray target = findPlayer(offlinePlayer.getUniqueId().toString()).get("graves").getAsJsonArray();
        ArrayList<Location> locs = new ArrayList<>();
        for(int i = 0; i < target.size(); i++){
            JsonObject graveDat = target.get(i).getAsJsonObject();
            JsonObject location = graveDat.get("location").getAsJsonObject();

            locs.add(new Location(Bukkit.getWorld(location.get("world").getAsString()), location.get("x").getAsDouble(), location.get("y").getAsDouble(), location.get("z").getAsDouble()));
        }

        return locs;
    }

    public boolean existsGraveAtLocation(Location l){
        for(JsonElement je: graveData){
            JsonArray target = je.getAsJsonObject().getAsJsonArray("graves");


            for(int i = 0; i < target.size(); i++){

                JsonObject location = target.get(i).getAsJsonObject().get("location").getAsJsonObject();
                if(location.get("x").getAsInt() == l.getBlockX() && location.get("y").getAsInt() == l.getBlockY()
                        && location.get("z").getAsInt() == l.getBlockZ() && location.get("world").getAsString().equals(l.getWorld().getName())){
                    return true;
                }
            }

        }

        return false;
    }

    public Grave getGraveAtLocation(Player p, Location l){
        Grave grave = new Grave();

        JsonArray target = findPlayer(p.getUniqueId().toString()).get("graves").getAsJsonArray();

        for(int i = 0; i < target.size(); i++){
            JsonObject graveDat = target.get(i).getAsJsonObject();
            JsonObject location = graveDat.get("location").getAsJsonObject();

            if(location.get("x").getAsInt() == l.getBlockX() && location.get("y").getAsInt() == l.getBlockY() && location.get("z").getAsInt() == l.getBlockZ()
                    && location.get("world").getAsString().equals(l.getWorld().getName())){

                grave.setDeathLocation(l);
                grave.setDeadPlayer(p);
                ItemStack[] storageContents = null, armor = null, offhand = null;

                try {
                    armor = InventorySerialization.itemStackArrayFromBase64(graveDat.get("armor").getAsString());
                    storageContents = InventorySerialization.itemStackArrayFromBase64(graveDat.get("content").getAsString());
                    offhand = InventorySerialization.itemStackArrayFromBase64(graveDat.get("offhand").getAsString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                grave.setPlayerDeathInventoryArmor(armor);
                grave.setPlayerDeathInventoryStorageContents(storageContents);
                grave.setPlayerDeathInventoryOffhand(offhand);
                grave.setPrevMaterial(Material.getMaterial(graveDat.get("material").getAsString()));

                return grave;

            }

        }

        return null;

    }

    public void addGrave(Grave grave){

        Player died = grave.getDeadPlayer();

        JsonObject targetObj = findPlayer(died.getUniqueId().toString());

        JsonObject newGrave = createGrave(grave);

        if(targetObj == null){
            targetObj = new JsonObject();
            targetObj.addProperty("name", died.getUniqueId().toString());

            JsonArray graves = new JsonArray();
            graves.add(newGrave);

            targetObj.add("graves", graves);

            graveData.add(targetObj);
        } else {
            JsonArray graves = targetObj.get("graves").getAsJsonArray();
            graves.add(newGrave);
        }

        writeFile();

    }

    public void removeGrave(Player recovered, Grave grave){
        JsonObject targetObj = findPlayer(recovered.getUniqueId().toString());

        if(targetObj != null){
            JsonArray graves = targetObj.get("graves").getAsJsonArray();
            for(int i = 0; i < graves.size(); i++){

                JsonObject location = graves.get(i).getAsJsonObject().get("location").getAsJsonObject();
                if(location.get("x").getAsInt() == grave.getDeathLocation().getBlockX() && location.get("y").getAsInt() == grave.getDeathLocation().getBlockY()
                        && location.get("z").getAsInt() == grave.getDeathLocation().getBlockZ() && location.get("world").getAsString().equals(grave.getDeathLocation().getWorld().getName())){
                    graves.remove(i);
                    break;
                }
            }
        }

        writeFile();
    }

    private JsonObject createGrave(Grave grave){
        String[] playerInventory = InventorySerialization.playerInventoryToBase64(grave.getDeadPlayer().getInventory());

        JsonObject newGrave = new JsonObject();
        newGrave.addProperty("content", playerInventory[0]);
        newGrave.addProperty("armor", playerInventory[1]);
        newGrave.addProperty("offhand", playerInventory[2]);
        newGrave.addProperty("material", grave.getPrevMaterial().toString());

        JsonObject location = new JsonObject();
        location.addProperty("x", grave.getDeathLocation().getBlockX());
        location.addProperty("y", grave.getDeathLocation().getBlockY());
        location.addProperty("z", grave.getDeathLocation().getBlockZ());
        location.addProperty("world", grave.getDeathLocation().getWorld().getName());

        newGrave.add("location", location);

        return newGrave;
    }


    public void prune(CommandSender cmdSend) {
        ArrayList<String> worldsLoaded = new ArrayList<>();

        for(World w: Bukkit.getWorlds()){
            worldsLoaded.add(w.getName());
        }

        cmdSend.sendMessage("Loaded Worlds: " + worldsLoaded.toString());

        for(JsonElement je: graveData){
            JsonArray target = je.getAsJsonObject().getAsJsonArray("graves");
            cmdSend.sendMessage("Pruned " + je.getAsJsonObject().get("name"));
            for(int i = 0; i < target.size(); i++){
                if(!worldsLoaded.contains(target.get(i).getAsJsonObject().get("location").getAsJsonObject().get("world").getAsString())){
                    cmdSend.sendMessage("Grave " + i);
                    target.remove(i);

                    i = 0;
                }
            }
        }

        cmdSend.sendMessage("Pruning Complete!");

        writeFile();


    }


}
