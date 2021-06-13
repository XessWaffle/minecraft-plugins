package cmd;

import gravemanager.GraveListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandGraveToggle implements CommandExecutor {

    private GraveListener gl;

    public CommandGraveToggle(GraveListener gl){
        this.gl = gl;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(s.equals("gravetoggle")){
            if (gl.isEnabled()) {
                gl.disable();

                commandSender.sendMessage("Graves disabled");

            } else {
                gl.enable();

                commandSender.sendMessage("Graves enabled");
            }

            return true;
        }

        return false;
    }
}
