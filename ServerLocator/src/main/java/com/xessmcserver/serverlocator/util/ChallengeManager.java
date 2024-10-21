package com.xessmcserver.serverlocator.util;

import com.xessmcserver.serverlocator.ServerLocator;
import com.xessmcserver.serverlocator.util.runnables.TeleporterRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChallengeManager{

    public static final int DISTANCE_TIER_WHY = 10;

    public static final int DISTANCE_TIER_BITCH = 100;

    public static final int DISTANCE_TIER_HARD = 1000;

    public static final int DISTANCE_TIER_MEDIUM = 10000;

    private static ServerLocator ref;

    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(6);

    private static ConcurrentHashMap<String, Location> playerHomes;

    private ConcurrentHashMap<String, Integer> enqueuedChallengers;

    public ChallengeManager(ServerLocator ref)
    {
        enqueuedChallengers = new ConcurrentHashMap<>();
        playerHomes = new ConcurrentHashMap<>();

        try {
            HashMap<String, String> loadedMap = loadHashMap(ref.getDataFolder() + "/PlayerHomeLocations.dat");

            for(String key: loadedMap.keySet()) {

                String[] locationData = loadedMap.get(key).split(",");
                playerHomes.put(key, new Location(Bukkit.getWorld(locationData[0]),
                                                    Integer.parseInt(locationData[1]),
                                                    Integer.parseInt(locationData[2]),
                                                    Integer.parseInt(locationData[3])));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        registerChallenges();

        this.ref = ref;
    }

    private void registerChallenges() {
        ChallengeGenerator.registerChallenge(ChallengeType.NONE, ChallengeTier.WHY, ChallengeTier.BITCH, ChallengeTier.HARD, ChallengeTier.MEDIUM, ChallengeTier.EASY);
        ChallengeGenerator.registerChallenge(ChallengeType.CONFUSION, ChallengeTier.WHY, ChallengeTier.BITCH, ChallengeTier.HARD, ChallengeTier.MEDIUM);
        ChallengeGenerator.registerChallenge(ChallengeType.DROP, ChallengeTier.BITCH, ChallengeTier.HARD, ChallengeTier.MEDIUM, ChallengeTier.EASY);
        ChallengeGenerator.registerChallenge(ChallengeType.GO, ChallengeTier.BITCH, ChallengeTier.HARD, ChallengeTier.MEDIUM, ChallengeTier.EASY);
        //ChallengeGenerator.registerChallenge(ChallengeType.FIGHT, ChallengeTier.HARD, ChallengeTier.MEDIUM, ChallengeTier.EASY);
    }

    public boolean startChallenge(Player challenger)
    {
        if(enqueuedChallengers.containsKey(challenger.getUniqueId().toString()) &&
                enqueuedChallengers.get(challenger.getUniqueId().toString()) > 0)
            return false;

        enqueuedChallengers.putIfAbsent(challenger.getUniqueId().toString(), 0);
        enqueuedChallengers.put(challenger.getUniqueId().toString(), enqueuedChallengers.get(challenger.getUniqueId().toString()) + 1);

        Challenge playerChallenge = determineChallenge(challenger);
        long challengeStartDelay = playerChallenge.getDelay();

        final Runnable challengeService = new Runnable() {
            @Override
            public void run() {
                try {
                    playerChallenge.onChallengeStart();
                    while (!playerChallenge.challenge()) {
                        if(!challenger.isOnline()) {
                            Bukkit.broadcastMessage(challenger.getName() + " left the server while trying to get home :(");
                            break;
                        }
                    }

                    if(challenger.isOnline()) {
                        boolean shouldTeleport = playerChallenge.onChallengeComplete();

                        if (shouldTeleport)
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ref, new TeleporterRunnable(challenger, playerChallenge.getTeleportDestination()));
                    }
                } catch(Exception e){
                    Bukkit.getServer().broadcastMessage(challenger.getName() + " did something weird during the " + playerChallenge.getChallengeName() + " challenge");
                    e.printStackTrace();
                }
                enqueuedChallengers.put(challenger.getUniqueId().toString(), enqueuedChallengers.get(challenger.getUniqueId().toString()) - 1);
            }
        };

        final Runnable countdownService = new Runnable() {
            @Override
            public void run() {

                long startTime = System.currentTimeMillis(),
                        currentTime = System.currentTimeMillis(),
                        printedSeconds = challengeStartDelay,
                        seconds = challengeStartDelay - ((currentTime - startTime) / 1000);

                while(currentTime - startTime < challengeStartDelay * 1000) {
                    printedSeconds = seconds;
                    seconds = challengeStartDelay - ((currentTime - startTime) / 1000);

                    if(printedSeconds != seconds)
                        challenger.sendTitle(ChatColor.RED + "" + seconds + " seconds", "", 5, 80, 10);

                    currentTime = System.currentTimeMillis();
                }

                service.schedule(challengeService, 0, TimeUnit.SECONDS);
            }
        };

        service.schedule(countdownService, 1, TimeUnit.SECONDS);

        return true;
    }


    private Challenge determineChallenge(Player challenger){
        Location currentPlayerLocation = challenger.getLocation();
        Location teleportLocation = playerHomes.get(challenger.getName());
        double distance = 0;
        Challenge challenge;

        if(teleportLocation == null)
            teleportLocation = challenger.getBedSpawnLocation();

        if(teleportLocation == null)
            teleportLocation = challenger.getWorld().getSpawnLocation();

        distance = currentPlayerLocation.distance(teleportLocation);

        if(distance < DISTANCE_TIER_WHY) {
            challenge = ChallengeGenerator.pickChallenge(challenger, ChallengeTier.WHY);
        } else if (distance > DISTANCE_TIER_WHY && distance < DISTANCE_TIER_BITCH) {
            challenge = ChallengeGenerator.pickChallenge(challenger, ChallengeTier.BITCH);
        } else if (distance > DISTANCE_TIER_BITCH && distance < DISTANCE_TIER_HARD) {
            challenge = ChallengeGenerator.pickChallenge(challenger, ChallengeTier.HARD);
        } else if (distance > DISTANCE_TIER_HARD && distance < DISTANCE_TIER_MEDIUM) {
            challenge = ChallengeGenerator.pickChallenge(challenger, ChallengeTier.MEDIUM);
        } else {
            challenge = ChallengeGenerator.pickChallenge(challenger, ChallengeTier.EASY);
        }

        challenge.setTeleportDestination(teleportLocation);
        challenge.ref = ref;

        return challenge;
    }

    public void setHomeLocation(String player, Location home){
        playerHomes.put(player, home);

        HashMap<String, String> toSave = new HashMap<>();

        for(String key: playerHomes.keySet()) {

            Location playerHome = playerHomes.get(key);

            String combine = "<name>,<x>,<y>,<z>";
            String result = combine.replace("<name>", playerHome.getWorld().getName())
                    .replace("<x>", "" + playerHome.getBlockX())
                    .replace("<y>", "" + playerHome.getBlockY())
                    .replace("<z>", "" + playerHome.getBlockZ());

            toSave.put(key, result);
        }

        try {
            saveHashMap(toSave, ref.getDataFolder() + "/PlayerHomeLocations.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveHashMap(HashMap<String, String> map, String fileName) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(map);
        oos.close();
    }

    public static HashMap<String, String> loadHashMap(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        HashMap<String, String> map = (HashMap<String, String>) ois.readObject();
        ois.close();
        return map;
    }


}
