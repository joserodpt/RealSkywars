package josegamerpt.realskywars.player;

import josegamerpt.realskywars.managers.MapManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityEvents implements Listener {

    @EventHandler
    public void EntitySpawnEvent(org.bukkit.event.entity.CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            e.setCancelled(true);
        }

        for (String s : MapManager.getRegisteredMaps()) {
            if (e.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(s)) {
                switch (e.getEntityType()) {
                    case BAT:
                    case CAT:
                    case CHICKEN:
                    case COD:
                    case COW:
                    case DONKEY:
                    case FOX:
                    case HORSE:
                    case MUSHROOM_COW:
                    case MULE:
                    case OCELOT:
                    case PARROT:
                    case PIG:
                    case RABBIT:
                    case SALMON:
                    case SHEEP:
                    case SKELETON_HORSE:
                    case SNOWMAN:
                    case SQUID:
                    case TROPICAL_FISH:
                    case PUFFERFISH:
                    case TURTLE:
                    case VILLAGER:
                    case WANDERING_TRADER:
                    case ENDER_DRAGON:
                        e.setCancelled(false);
                    default:
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