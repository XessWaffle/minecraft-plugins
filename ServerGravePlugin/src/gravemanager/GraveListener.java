package gravemanager;

import io.GraveData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import utils.Grave;

import java.util.ArrayList;

public class GraveListener implements Listener {

    private boolean enabled;
    private GraveData graveData;

    public GraveListener(GraveData gd) {
        enabled = true;
        this.graveData = gd;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(enabled) {
            Grave grave = new Grave(event.getEntity());
            graveData.addGrave(grave);

            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(enabled) {
            try {
                if(event.getClickedBlock().getType() == Material.CHEST) {
                    Grave recover = graveData.getGraveAtLocation(event.getPlayer(), event.getClickedBlock().getLocation());
                    if (recover != null) {
                        recover.open(event.getPlayer());
                        graveData.removeGrave(event.getPlayer(), recover);
                    }
                }
            } catch(Exception e){
                //null pointer
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        for (Block block : new ArrayList<>(event.blockList())){
            if(block.getType() == Material.CHEST){
                if(graveData.existsGraveAtLocation(block.getLocation())){
                    event.blockList().remove(block);
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

    public boolean isEnabled(){
        return enabled;
    }
}