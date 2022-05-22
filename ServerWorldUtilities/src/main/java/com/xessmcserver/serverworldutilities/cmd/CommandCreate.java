package com.xessmcserver.serverworldutilities.cmd;

import com.xessmcserver.serverworldutilities.io.PlayerData;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.xessmcserver.serverworldutilities.ServerWorldUtilities;
import com.xessmcserver.serverworldutilities.util.WorldList;

public class CommandCreate implements CommandExecutor {

    private static final String DEFAULT_NAME = "Terrain";

    private WorldList worlds;

    public ServerWorldUtilities ref;
    public PlayerData dataStore;

    public CommandCreate(WorldList worlds){
        this.worlds = worlds;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {

        if(label.equals("create") && strings.length == 1){
            int iters = 0;

            String check = strings[0], name = strings[0];

            if(strings[0].equals("")){
                name = DEFAULT_NAME;
            }

            while(worlds.hasWorld(name)){
                name = check + "-" + iters;
                iters++;
            }

            WorldCreator nwc = new WorldCreator(name);

            nwc.environment(World.Environment.NORMAL);
            nwc.type(WorldType.NORMAL);

            commandSender.sendMessage("Creating World...");

            World w = nwc.createWorld();

            worlds.addWorld(name, w);
            commandSender.sendMessage("Created World as: " + name);

            dataStore.addWorld(w);

            return true;
        }

        return false;
    }
}
