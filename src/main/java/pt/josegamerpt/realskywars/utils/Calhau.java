package pt.josegamerpt.realskywars.utils;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class Calhau {

    private Location location;
    private Material material;
    private byte data;
	private Collection<ItemStack> drops;
   
    @SuppressWarnings("deprecation")
    public Calhau(Block block){
        this.location = block.getLocation();
        this.material = block.getType();
        this.data = block.getData();
        this.drops = block.getDrops();
    }
   
    public Location getLocation(){
        return location;
    }
   
    public Material getMaterial(){
        return material;
    }
   
    public Collection<ItemStack> getDrops(){
        return drops;
    }

}