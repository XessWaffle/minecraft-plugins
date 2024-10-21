package com.xessmcserver.serverlocator;

import com.xessmcserver.serverlocator.cmd.CommandHome;
import com.xessmcserver.serverlocator.cmd.CommandMark;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerLocator extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();

        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        CommandHome homeExec = new CommandHome(this);

        // Plugin startup logic
        getCommand("mark").setExecutor(new CommandMark(this));
        getCommand("home").setExecutor(homeExec);
        getCommand("sethome").setExecutor(homeExec);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}