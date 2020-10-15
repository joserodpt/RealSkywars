package pt.josegamerpt.realskywars.player;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import pt.josegamerpt.realskywars.managers.MapManager;

public class EntityEvents implements Listener {

    @EventHandler
    public void EntitySpawnEvent(org.bukkit.event.entity.CreatureSpawnEvent e) {
        for (String s : MapManager.getRegisteredMaps()) {
            if (e.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(s)) {
                switch (e.getEntityType()) {
                    case ZOMBIE:
                    case SPIDER:
                    case SKELETON:
                    case CREEPER:
                    case VINDICATOR:
                    case CAVE_SPIDER:
                    case GUARDIAN:
                    case ELDER_GUARDIAN:
                    case ENDERMAN:
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