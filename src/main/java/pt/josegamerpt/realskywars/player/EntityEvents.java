package pt.josegamerpt.realskywars.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityChangeBlockEvent;
import pt.josegamerpt.realskywars.managers.MapManager;

public class EntityEvents implements Listener {

    @EventHandler
    public void EntitySpawnEvent(org.bukkit.event.entity.CreatureSpawnEvent e) {
        for (String s : MapManager.getRegisteredMaps()) {
            if (e.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(s)) {
                if (e.getEntity().getType() == EntityType.ENDERMAN) {
                    e.setCancelled(true);
                }
                if (e.getEntity().getType() == EntityType.CREEPER) {
                    e.setCancelled(true);
                }
                if (e.getEntity().getType() == EntityType.SKELETON) {
                    e.setCancelled(true);
                }
                if (e.getEntity().getType() == EntityType.SPIDER) {
                    e.setCancelled(true);
                }
                if (e.getEntity().getType() == EntityType.ZOMBIE) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void blockChangeEvent(EntityChangeBlockEvent e) {
        Entity ent = e.getEntity();

        if (ent.hasMetadata("trailBlock")) {
            e.setCancelled(true);
        }
    }
}