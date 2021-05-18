package sleep;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;

public class ServerSleepPlugin extends JavaPlugin{
	
	public SleepListener sl = new SleepListener();
	
	@Override
	public void onEnable() {
		sl.enable();
		getServer().getPluginManager().registerEvents(sl, this);
	}
	
	@Override
	public void onDisable() {
		sl.disable();
	}
}

class SleepListener implements Listener{
	
	private boolean enabled;
	
	public SleepListener() {
		enabled = true;
	}
	
	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		if(event.getBedEnterResult() == BedEnterResult.OK && enabled) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/time set 500");
		}
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
}
