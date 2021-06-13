package utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class Grave {

    private Material prev;

    private ItemStack[] storageContents;
    private ItemStack[] armor;
    private ItemStack[] offhandContents;
    private Player died;

    private Location deathLocation;

    public Grave(Player player){
        this.deathLocation = player.getLocation();

        this.died = player;

        this.prev = this.deathLocation.getBlock().getType();
        this.deathLocation.getBlock().setType(Material.CHEST);

        this.storageContents = player.getInventory().getStorageContents();
        this.armor = player.getInventory().getArmorContents();
    }

    public Grave(){

    }

    public void open(Player player){

        if(player.getUniqueId().toString().equals(this.died.getUniqueId().toString())){

            for(int i = 0; i < player.getInventory().getSize(); i++){
                player.getInventory().setItemInMainHand(player.getInventory().getItem(i));
                player.dropItem(true);
            }

            player.getInventory().setItemInMainHand(player.getInventory().getItemInOffHand());
            player.dropItem(true);

            for(ItemStack items: player.getInventory().getArmorContents()){
                player.getInventory().setItemInMainHand(items);
                player.dropItem(true);
            }

            player.getInventory().clear();

            player.getInventory().setStorageContents(this.storageContents);
            player.getInventory().setArmorContents(this.armor);
            player.getInventory().setExtraContents(this.offhandContents);

            this.deathLocation.getBlock().setType(prev);

        }
    }


    public Material getPrevMaterial() {
        return prev;
    }

    public void setPrevMaterial(Material prev) {
        this.prev = prev;
    }

    public ItemStack[] getPlayerDeathInventoryStorageContents() {
        return this.storageContents;
    }

    public void setPlayerDeathInventoryStorageContents(ItemStack[] storageContents) {
        this.storageContents = storageContents;
    }

    public ItemStack[] getPlayerDeathInventoryArmor(){
        return armor;
    }

    public void setPlayerDeathInventoryArmor(ItemStack[] armor){
        this.armor = armor;
    }

    public ItemStack[] getPlayerDeathInventoryOffhand(){
        return offhandContents;
    }

    public void setPlayerDeathInventoryOffhand(ItemStack[] offhand){
        this.offhandContents = offhand;
    }

    public Location getDeathLocation(){
        return this.deathLocation;
    }

    public void setDeathLocation(Location loc){
        this.deathLocation = loc;
    }

    public Player getDeadPlayer(){
        return this.died;
    }

    public void setDeadPlayer(Player dead){
        this.died = dead;
    }
}
