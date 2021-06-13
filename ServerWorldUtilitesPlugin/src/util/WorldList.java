package util;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class WorldList {

    private static final String FILE_NAME = "worldlist.json";

    private JsonArray listWorlds;
    private HashMap<String, World> worldList;

    private PluginCore ref;

    public WorldList(PluginCore ref){
        worldList = new HashMap<>();

        JsonArray tempList = new JsonArray();
        listWorlds = new JsonArray();

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
                tempList = jp.parse(new JsonReader(isr)).getAsJsonArray();

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        for(World w: Bukkit.getWorlds()){
            worldList.put(w.getName(), w);
            listWorlds.add(w.getName());
        }

        listWorlds.addAll(tempList);

        for(int i = 0; i < listWorlds.size(); i++){
            WorldCreator wc = new WorldCreator(listWorlds.get(i).getAsString());
            worldList.put(listWorlds.get(i).getAsString(), wc.createWorld());
        }

    }

    private void writeFile() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String toWrite = gson.toJson(listWorlds);

        File create = new File(ref.getDataFolder() + "/" + FILE_NAME);

        try{
            FileWriter myWriter = new FileWriter(create);
            myWriter.write(toWrite);
            myWriter.close();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public World getWorld(String name){
        return worldList.get(name);
    }

    public boolean hasWorld(String name){
        return worldList.containsKey(name);
    }

    public void addWorld(String name, World w){
        worldList.put(name, w);
        listWorlds.add(name);

        writeFile();
    }

    public World removeWorld(String name){

        for(int i = 0; i < listWorlds.size(); i++){
            if(listWorlds.get(i).getAsString().equals(name)){
                listWorlds.remove(i);
                break;
            }
        }

        writeFile();

        return worldList.remove(name);
    }

    public Collection<World> getWorlds(){
        return worldList.values();
    }

}
