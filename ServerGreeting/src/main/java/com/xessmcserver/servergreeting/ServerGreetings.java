package com.xessmcserver.servergreeting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;


public class ServerGreetings extends JavaPlugin {

    private GreetingsListener gl = new GreetingsListener();

    @Override
    public void onEnable() {
        gl.enable();
        getServer().getPluginManager().registerEvents(gl, this);

        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        try {
            IPLookup.initializeIPDB(getDataFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        gl.disable();
    }

}

class GreetingsListener implements Listener {

    private ArrayList<String> welcomeMessages, deathMessages, respawnMessages;
    private boolean enabled;

    public GreetingsListener() {
        welcomeMessages = new ArrayList<>();
        deathMessages = new ArrayList<>();
        respawnMessages = new ArrayList<>();

        welcomeMessages.add("XessMCServer welcomes you <name>");
        welcomeMessages.add("Wassup <name>? how ya doin?");
        welcomeMessages.add("The game summons <name>!");
        welcomeMessages.add("Everyone prepare your assholes, <name> has joined the server!");
        welcomeMessages.add("Nerrrrrrrrrrrddd --><name><---");
        welcomeMessages.add("Fuck you <name>!");
        welcomeMessages.add("Sauren's mom is looking for you, <name>!");
        welcomeMessages.add("o_O <name> O_o");
        welcomeMessages.add("~uwu~ <name> :)");
        welcomeMessages.add("God help us, <name> is here :(");

        deathMessages.add("You fuckin suck <name>");
        deathMessages.add(   "Sauren's mom sat on <name>'s ass!");
        deathMessages.add("How fucked up were you <name>???");
        deathMessages.add("You fuckin deserved it <name>.");
        deathMessages.add("You have been dominated by some fucking code <name>.");
        deathMessages.add("Bruh Bruh (<name> died)");
        deathMessages.add("What the fuck <name>");
        deathMessages.add("Leave the server now, <name>");

        respawnMessages.add("Finally back from the dead <name>?");
        respawnMessages.add("<name> lives to fight another day!");
        respawnMessages.add("Took you long enough, <name>.");
        respawnMessages.add("Are you Jesus <name>??!!");
        respawnMessages.add("Don't die again <name>");


        enabled = true;

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        InetAddress address = event.getPlayer().getAddress().getAddress();
        String country = IPLookup.getIPLocation(address);
        Player player = event.getPlayer();

        String[] opList = new String[]{
                "XessWaffle"
        };

        ArrayList<String> ops = new ArrayList<>(Arrays.asList(opList));

        String[] lightInsults = {
                "Nice try, but even my grandma could spot that spoof!",
                "Did you really think that would work? Bless your heart.",
                "You're about as sneaky as a neon sign.",
                "Is that the best you can do? Cute.",
                "I've seen better attempts from a potato.",
                "You must be new here. Welcome to the big leagues.",
                "That was adorable. Try again.",
                "Spoofing? Really? How original.",
                "You call that a hack? My cat could do better.",
                "Nice effort, but you're still a rookie."
        };

        if(!address.isSiteLocalAddress() && ops.contains(player.getName())) {
            player.setOp(false);
            player.setGameMode(GameMode.ADVENTURE);
            player.setWalkSpeed(0.0f);
            player.chat("I am a little bitchass spoofer trying to fuck with a minecraft server cuz I don't have anything better to do with my life!");
            sendTitleToAllPlayers(ChatColor.BOLD + "Spoofing Detected", ChatColor.RED + player.getName());
            Bukkit.broadcastMessage(ChatColor.YELLOW + lightInsults[(int)(Math.random() * lightInsults.length)] + ChatColor.WHITE);
        } else if(!country.equals("US") && !ops.contains(player.getName())) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setWalkSpeed(0.0f);
            sendTitleToAllPlayers(ChatColor.BOLD + "Potential Spoofing Detected", ChatColor.RED + player.getName());
            Bukkit.broadcastMessage(ChatColor.YELLOW + lightInsults[(int) (Math.random() * lightInsults.length)] + ChatColor.WHITE);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
            player.setWalkSpeed(0.2f);
            int rand = (int)(Math.random() * welcomeMessages.size());
            String broadcast = welcomeMessages.get(rand).replace("<name>", ChatColor.YELLOW + event.getPlayer().getName() + ChatColor.WHITE);
            Bukkit.broadcastMessage(broadcast);

            sendTitleToAllPlayers("Welcome " + ChatColor.AQUA + event.getPlayer().getName(), ChatColor.GREEN + "Online: " + Bukkit.getOnlinePlayers().size());

            if(ops.contains(player.getName())) {
                player.setOp(true);
            }

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(enabled) {
            int rand = (int)(Math.random() * deathMessages.size());
            String broadcast = deathMessages.get(rand).replace("<name>", ChatColor.RED + event.getEntity().getName() + ChatColor.WHITE);

            Bukkit.broadcastMessage(broadcast);
            sendTitleToAllPlayers(ChatColor.WHITE + event.getEntity().getName() + ChatColor.RED + " died", "");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(enabled) {
            int rand = (int)(Math.random() * respawnMessages.size());
            String broadcast = respawnMessages.get(rand).replace("<name>", ChatColor.BLUE + event.getPlayer().getName() + ChatColor.WHITE);

            Bukkit.broadcastMessage(broadcast);
        }
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public void sendTitleToAllPlayers(String title, String subtitle){
        for(Player p: Bukkit.getOnlinePlayers()){
            p.sendTitle(title, subtitle, 5, 80, 10);
        }
    }
}
