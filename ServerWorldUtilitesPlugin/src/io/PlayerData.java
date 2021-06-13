package io;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import util.PluginCore;
import util.WorldList;

import java.io.*;
import java.util.Scanner;

public class PlayerData {

    private static final String FILE_NAME = "playerdata.json";
    private JsonArray playerData;

    private PluginCore ref;
    private WorldList worlds;

    public PlayerData(WorldList worlds, PluginCore ref){

        this.worlds = worlds;

        playerData = new JsonArray();
        this.ref = ref;

        File create = new File(ref.getDataFolder() + "/" + FILE_NAME);

        if(!create.exists()){
            try {
                create.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            try {
                InputStream is = new FileInputStream(create);
                InputStreamReader isr = new InputStreamReader(is);

                JsonParser jp = new JsonParser();
                playerData = jp.parse(new JsonReader(isr)).getAsJsonArray();



            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        verifyPlayers();
    }

    public void refresh(){
        verifyPlayers();
    }

    private void verifyPlayers() {
        for(Player p: Bukkit.getOnlinePlayers()){
            if(!hasPlayer(p.getUniqueId().toString())){

                JsonObject worldsProps = new JsonObject();

                for(World w: worlds.getWorlds()){

                    JsonObject worldProps = new JsonObject();
                    JsonObject location = new JsonObject();
                    JsonObject respawnLocation = new JsonObject();

                    if(p.getWorld().getName().equals(w.getName())) {
                        location.addProperty("x", p.getLocation().getX());
                        location.addProperty("y", p.getLocation().getY());
                        location.addProperty("z", p.getLocation().getZ());
                        location.addProperty("yaw", p.getLocation().getYaw());
                        location.addProperty("pitch", p.getLocation().getPitch());
                    } else {
                        location.addProperty("x", w.getSpawnLocation().getX());
                        location.addProperty("y", w.getSpawnLocation().getY());
                        location.addProperty("z", w.getSpawnLocation().getZ());
                        location.addProperty("yaw", 0);
                        location.addProperty("pitch", 0);

                    }

                    respawnLocation.addProperty("x", w.getSpawnLocation().getX());
                    respawnLocation.addProperty("y", w.getSpawnLocation().getY());
                    respawnLocation.addProperty("z", w.getSpawnLocation().getZ());
                    respawnLocation.addProperty("yaw", 0);
                    respawnLocation.addProperty("pitch", 0);

                    worldProps.add("location", location);
                    worldProps.add("respawnLocation", respawnLocation);
                    worldProps.addProperty("available", true);

                    worldsProps.add(w.getName(), worldProps);
                }

                JsonObject playerObj = new JsonObject();
                playerObj.addProperty("name", p.getUniqueId().toString());
                playerObj.addProperty("currentWorld", p.getWorld().getName());
                playerObj.add("worlds", worldsProps);


                playerData.add(playerObj);
            }
        }

        writeFile();
    }

    public boolean hasPlayer(String uuid){
        for(int i = 0; i < playerData.size(); i++){
            JsonObject in = playerData.get(i).getAsJsonObject();

            if(in.get("name").getAsString().equals(uuid)){
               return true;
            }

        }

        return false;
    }

    public JsonObject findPlayer(String uuid){
        for(int i = 0; i < playerData.size(); i++){
            JsonObject in = playerData.get(i).getAsJsonObject();

            if(in.get("name").getAsString().equals(uuid)){
                return in;
            }

        }

        return null;
    }

    private void writeFile() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String toWrite = gson.toJson(playerData);

        File create = new File(ref.getDataFolder() + "/" + FILE_NAME);

        try{
            FileWriter myWriter = new FileWriter(create);
            myWriter.write(toWrite);
            myWriter.close();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setLocation(String uuid, String world, Location location){
        JsonObject targetObj = findPlayer(uuid).get("worlds").getAsJsonObject().get(world).getAsJsonObject().get("location").getAsJsonObject();

        targetObj.addProperty("x", location.getX());
        targetObj.addProperty("y", location.getY());
        targetObj.addProperty("z", location.getZ());
        targetObj.addProperty("yaw", location.getYaw());
        targetObj.addProperty("pitch", location.getPitch());

        writeFile();
    }

    public Location getLocation(String uuid, World world){
        try{
            JsonObject targetObj = findPlayer(uuid).get("worlds").getAsJsonObject().get(world.getName()).getAsJsonObject().get("location").getAsJsonObject();
            Location nLoc = new Location(world, targetObj.get("x").getAsDouble(), targetObj.get("y").getAsDouble(), targetObj.get("z").getAsDouble());

            nLoc.setPitch(targetObj.get("pitch").getAsFloat());
            nLoc.setYaw(targetObj.get("yaw").getAsFloat());
            return nLoc;
        } catch(Exception e){
            return world.getSpawnLocation();
        }
    }

    public void addWorld(World world){
        for(int i = 0; i < playerData.size(); i++){
            JsonObject targetObj = playerData.get(i).getAsJsonObject().get("worlds").getAsJsonObject();

            JsonObject worldProps = new JsonObject();
            JsonObject location = new JsonObject();
            JsonObject respawnLocation = new JsonObject();

            location.addProperty("x", world.getSpawnLocation().getX());
            location.addProperty("y", world.getSpawnLocation().getY());
            location.addProperty("z", world.getSpawnLocation().getZ());
            location.addProperty("yaw", 0);
            location.addProperty("pitch", 0);

            respawnLocation.addProperty("x", world.getSpawnLocation().getX());
            respawnLocation.addProperty("y", world.getSpawnLocation().getY());
            respawnLocation.addProperty("z", world.getSpawnLocation().getZ());
            respawnLocation.addProperty("yaw", 0);
            respawnLocation.addProperty("pitch", 0);

            worldProps.add("location", location);
            worldProps.add("respawnLocation", location);

            targetObj.add(world.getName(), worldProps);
            targetObj.addProperty("available", true);
        }


        writeFile();
    }

    public void removeWorld(String world){
        for(int i = 0; i < playerData.size(); i++){
            JsonObject targetObj = playerData.get(i).getAsJsonObject().get("worlds").getAsJsonObject();
            targetObj.addProperty("available", false);
        }

        writeFile();
    }

    public void invokeRespawn(PlayerRespawnEvent event){
        JsonObject target = findPlayer(event.getPlayer().getUniqueId().toString());
        JsonObject world = target.get("worlds").getAsJsonObject().get(target.get("currentWorld").getAsString()).getAsJsonObject();

        JsonObject respawnLocation = world.get("respawnLocation").getAsJsonObject();

        Location respawn = new Location(Bukkit.getWorld(target.get("currentWorld").getAsString()), respawnLocation.get("x").getAsDouble(),respawnLocation.get("y").getAsDouble(),respawnLocation.get("z").getAsDouble());

        event.setRespawnLocation(respawn);
    }

    public void setRespawn(Player p, Location l){
        JsonObject target = findPlayer(p.getUniqueId().toString());
        JsonObject world = target.get("worlds").getAsJsonObject().get(target.get("currentWorld").getAsString()).getAsJsonObject();

        JsonObject respawnLocation = world.get("respawnLocation").getAsJsonObject();

        respawnLocation.addProperty("x", l.getX());
        respawnLocation.addProperty("y", l.getY());
        respawnLocation.addProperty("z", l.getZ());
    }

    public void setCurrentWorld(Player p, World l){
        JsonObject target = findPlayer(p.getUniqueId().toString());

        target.addProperty("currentWorld", l.getName());
    }




}
