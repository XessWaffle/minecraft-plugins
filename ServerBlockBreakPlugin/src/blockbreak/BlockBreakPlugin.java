package blockbreak;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockBreakPlugin extends JavaPlugin{
	
	private CommandTarget cmdt = new CommandTarget();
	private TargetListener tl = new TargetListener(cmdt);

	@Override
	public void onEnable() {
		tl.enable();
		getCommand("target").setExecutor(cmdt);
		getCommand("untarget").setExecutor(cmdt);
		getCommand("targets").setExecutor(cmdt);
		getServer().getPluginManager().registerEvents(tl, this);
	}
	
	@Override
	public void onDisable() {
		tl.disable();
	}
	
}

class TargetListener implements Listener {
	
	private boolean enabled;
	private CommandTarget lookup;
	
	public TargetListener(CommandTarget lookup) {
		enabled = true;
		this.lookup = lookup;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(lookup.getTargets().contains(event.getPlayer().getName()) && enabled && Math.random() > 0.98) {
			Location expLoc = event.getBlock().getLocation();
			
			World w = event.getPlayer().getWorld();
			//w.createExplosion(expLoc, 2, false);
			event.getPlayer().teleport(new Location(w, expLoc.toVector().getX() + (int)((Math.random() - 0.5) * 10), expLoc.toVector().getY(), expLoc.toVector().getZ() + (int)((Math.random() - 0.5) * 10)));		
		}
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
}


