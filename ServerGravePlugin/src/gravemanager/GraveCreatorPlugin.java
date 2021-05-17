package gravemanager;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
public class GraveCreatorPlugin extends JavaPlugin{
	
	private GraveListener gl = new GraveListener();
	
	@Override
	public void onEnable() {
		gl.enable();
		getServer().getPluginManager().registerEvents(gl, this);
	}
	
	@Override
	public void onDisable() {
		gl.disable();
	}
}

class GraveListener implements Listener {
	
	private boolean enabled;
	
	public GraveListener() {
		enabled = true;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(enabled) {
			Location deathLoc = event.getEntity().getLocation();
			
			deathLoc.getBlock().setType(Material.CHEST);
			
			int itemsAdded = 0;
			
			Queue<ItemStack> items = new LinkedList<ItemStack>(event.getDrops());
			
			while(!items.isEmpty()) {	
				ItemStack stack = items.remove();
				event.getDrops().remove(stack);
				
				((Chest) deathLoc.getBlock().getState()).getInventory().addItem(stack);
				itemsAdded++;
				
				if(itemsAdded >= 27) {
					break;
				}
			}
			
			
		}
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
}
