package gravemanager;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import cmd.CommandGrave;
import cmd.CommandGraveToggle;
import io.GraveData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import utils.Grave;

public class GraveCreatorPlugin extends JavaPlugin{
	
	private GraveListener gl;

	private GraveData graveData;

	private CommandGraveToggle cmdGraveToggle;
	private CommandGrave cmdGrave;
	
	@Override
	public void onEnable() {

		if (!getDataFolder().exists())
			getDataFolder().mkdir();

		graveData = new GraveData(this);

		gl = new GraveListener(graveData);
		gl.enable();

		cmdGraveToggle = new CommandGraveToggle(gl);
		cmdGrave = new CommandGrave(graveData);

		getServer().getPluginManager().registerEvents(gl, this);

		this.getCommand("gravetoggle").setExecutor(cmdGraveToggle);
		this.getCommand("grave").setExecutor(cmdGrave);
	}
	
	@Override
	public void onDisable() {
		gl.disable();
	}

}

