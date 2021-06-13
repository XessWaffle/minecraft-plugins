package cmd;

import io.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import util.PluginCore;
import util.WorldList;

public class CommandSwitch implements CommandExecutor {

    private WorldList worlds;

    public PluginCore ref;
    public PlayerData dataStore;

    public CommandSwitch(WorldList worlds){
        this.worlds = worlds;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(s.equals("switch")){
            if(strings.length == 0){
                for(World w: worlds.getWorlds()){
                    commandSender.sendMessage(w.getName());
                }

                return true;
            } else {
                try{
                    World teleport = worlds.getWorld(strings[0]);

                    if(teleport == null){
                        commandSender.sendMessage(strings[0] + " does not exist");
                        return true;

                    } else {
                        Player sender = Bukkit.getServer().getPlayer(commandSender.getName());
                        World curr = sender.getWorld();

                        if(!curr.getName().equals(strings[0])) {
                            if(strings.length > 1 && strings[1].equals("all")) {
                                for(Player p: Bukkit.getOnlinePlayers()){
                                    dataStore.setLocation(p.getUniqueId().toString(), curr.getName(), p.getLocation());
                                    p.teleport(dataStore.getLocation(p.getUniqueId().toString(), teleport));
                                }
                            } else {
                                dataStore.setLocation(sender.getUniqueId().toString(), curr.getName(), sender.getLocation());
                                dataStore.setCurrentWorld(sender, teleport);
                                sender.teleport(dataStore.getLocation(sender.getUniqueId().toString(), teleport));

                            }
                        } else {
                            commandSender.sendMessage("Can't switch to same world");
                        }
                    }

                    return true;

                } catch(Exception e){
                    e.printStackTrace();
                    commandSender.sendMessage(strings[0] + " does not exist");
                }
            }
        }

        return false;
    }
}
