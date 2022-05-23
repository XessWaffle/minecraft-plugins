package com.xessmcserver.serverarbiterpower;

import com.xessmcserver.serverarbiterpower.cmd.CommandEnforce;
import com.xessmcserver.serverarbiterpower.io.DecreeManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerArbiterPower extends JavaPlugin{

    private DecreeManager decreeManager = new DecreeManager();
    private CommandEnforce cmdt = new CommandEnforce(decreeManager);

    @Override
    public void onEnable() {

        decreeManager.enable();

        getCommand("enforce").setExecutor(cmdt);
        getCommand("enforced").setExecutor(cmdt);
        getCommand("unenforce").setExecutor(cmdt);

        //getServer().getPluginManager().registerEvents(decreeManager, this);
    }

    @Override
    public void onDisable() {
        decreeManager.disable();
    }

}



