package serverstart;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerGreetingPlugin extends JavaPlugin{
	
	private GreetingsListener gl = new GreetingsListener();
	
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

class GreetingsListener implements Listener{
	
	private ArrayList<String> welcomeMessages, deathMessages, respawnMessages;
	private boolean enabled;
	
	public GreetingsListener() {
		welcomeMessages = new ArrayList<>();
		deathMessages = new ArrayList<>();
		respawnMessages = new ArrayList<>();
		
		welcomeMessages.add("Wassup <name>? how ya doin?");
		welcomeMessages.add("Everyone prepare your assholes, <name> has joined the server!");
		welcomeMessages.add("Nerrrrrrrrrrrddd --><name><---");
		welcomeMessages.add("Fuck you <name>!");
		welcomeMessages.add("Sauren's mom is looking for you, <name>!");
		welcomeMessages.add("o_O <name> O_o");
		
		deathMessages.add("You fuckin suck <name>");
		deathMessages.add("Sauren's mom sat on <name>'s ass!");
		deathMessages.add("How fucked up were you <name>???");
		deathMessages.add("You fuckin deserved it <name>.");
		deathMessages.add("You have been dominated by some fucking code <name>.");
		deathMessages.add("Bruh Bruh (<name> died)");
		
		respawnMessages.add("Finally back from the dead <name>?");
		respawnMessages.add("<name> lives to fight another day!");
		respawnMessages.add("Took you long enough, <name>.");
		respawnMessages.add("Are you Jesus <name>??!!");
		
		
		enabled = true;
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(enabled) {
			int rand = (int)(Math.random() * welcomeMessages.size());
			String broadcast = welcomeMessages.get(rand).replace("<name>", event.getPlayer().getName());
			
			Bukkit.broadcastMessage(broadcast);
		}
	}
	
	@EventHandler 
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(enabled) {
			int rand = (int)(Math.random() * deathMessages.size());
			String broadcast = deathMessages.get(rand).replace("<name>", event.getEntity().getName());
			
			Bukkit.broadcastMessage(broadcast);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if(enabled) {
			int rand = (int)(Math.random() * respawnMessages.size());
			String broadcast = respawnMessages.get(rand).replace("<name>", event.getPlayer().getName());
			
			Bukkit.broadcastMessage(broadcast);
		}
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
}
