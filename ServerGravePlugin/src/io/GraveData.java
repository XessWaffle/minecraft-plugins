package io;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import gravemanager.GraveCreatorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import utils.Grave;
import utils.InventorySerialization;

import java.io.*;
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

    public HashSet<Location> getGraves(Player p){
        JsonArray target = findPlayer(p.getUniqueId().toString()).get("graves").getAsJsonArray();
        HashSet<Location> locs = new HashSet<>();
        for(int i = 0; i < target.size(); i++){
            JsonObject graveDat = target.get(i).getAsJsonObject();
            JsonObject location = graveDat.get("location").getAsJsonObject();

            locs.add(new Location(Bukkit.getWorld(location.get("world").getAsString()), location.get("x").getAsDouble(), location.get("y").getAsDouble(), location.get("z").getAsDouble()));
        }

        return locs;

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
            graves.remove(createGrave(grave));
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


}
